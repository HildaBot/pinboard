package ch.jamiete.hilda.pinboard;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.configuration.Configuration;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;
import java.time.OffsetDateTime;

public class PinboardTask implements Runnable {
    GenericGuildMessageReactionEvent event;
    Configuration cfg;

    public PinboardTask(final GenericGuildMessageReactionEvent event, final Configuration cfg) {
        this.event = event;
        this.cfg = cfg;
    }

    @Override
    public void run() {
        final TextChannel send = this.event.getGuild().getTextChannelById(this.cfg.getString("pinboard", "12345"));

        if (send == null) {
            return;
        }

        final Message pin;

        try {
            pin = this.event.getChannel().getMessageById(this.event.getMessageId()).complete();
        } catch (final Exception e) {
            Hilda.getLogger().fine("The message that is to be pinned was lost.");
            return;
        }

        int age = this.cfg.getInteger("maxage", 0);

        if (age > 0) {
            OffsetDateTime latest = OffsetDateTime.now().minusDays(age);
            if (pin.getCreationTime().isBefore(latest)) {
                Hilda.getLogger().fine("Ignored message that was too old.");
                return;
            }
        }

        final MessageReaction reaction = pin.getReactions().stream().filter(r -> r.getEmote().getName().equals(PinboardPlugin.EMOTE)).findFirst().orElse(null);

        if (reaction == null) {
            Hilda.getLogger().fine("Couldn't find the relevant reaction on the message.");
            return;
        }

        final String entryid = this.cfg.getString(this.event.getMessageId(), null);
        int required = this.cfg.getInteger("required", 3);

        if (!send.isNSFW() && pin.getTextChannel().isNSFW() && !pin.getAttachments().isEmpty()) {
            Hilda.getLogger().fine("Ignored a message with an attachment from a NSFW channel");
            return;
        }

        int count = (int) reaction.getUsers().complete().stream().filter(u -> !u.getId().equals(pin.getAuthor().getId())).count();

        if (entryid != null) { // Pinboard entry exists
            Message entry = null;

            try {
                entry = send.getMessageById(entryid).complete();
            } catch (final Exception e) {
                this.cfg.setString(this.event.getMessageId(), null);
                Hilda.getLogger().fine("The entry was supposed to exist, but I couldn't find it.");
            }

            if (entry != null) {
                if (count < required) {
                    Hilda.getLogger().fine("Deleting a message for having too few reactions.");
                    entry.delete().queue();
                    return;
                } else {
                    Hilda.getLogger().fine("Editing a message to update its reaction count.");
                    entry.editMessage(PinboardUtil.build(reaction, pin)).queue();
                    return;
                }
            }
        }

        // No pinboard entry exists if this code is reached
        if (count < required) {
            Hilda.getLogger().fine("Did not reach threshold. (" + reaction.getCount() + "<" + required + ")");
            return;
        }

        Hilda.getLogger().fine("Adding a new pin message.");
        send.sendMessage(PinboardUtil.build(reaction, pin)).queue(sent -> {
            this.cfg.setString(this.event.getMessageId(), sent.getId());
        });
    }

}
