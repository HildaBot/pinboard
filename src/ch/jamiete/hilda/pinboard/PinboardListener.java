package ch.jamiete.hilda.pinboard;

import java.util.Iterator;
import java.util.Map.Entry;
import com.google.gson.JsonElement;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.CommandManager;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.events.EventHandler;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;

public class PinboardListener {
    private final Hilda hilda;
    private final PinboardPlugin plugin;

    public PinboardListener(final Hilda hilda, final PinboardPlugin plugin) {
        this.hilda = hilda;
        this.plugin = plugin;
    }

    @EventHandler
    public void onMessageDelete(final GuildMessageDeleteEvent event) {
        final Configuration cfg = this.plugin.getHilda().getConfigurationManager().getConfiguration(this.plugin, event.getGuild().getId());
        final TextChannel send = event.getGuild().getTextChannelById(cfg.getString("pinboard", "12345"));

        if (send == null || !send.equals(event.getChannel())) {
            return;
        }

        final Iterator<Entry<String, JsonElement>> iterator = cfg.get().entrySet().iterator();

        while (iterator.hasNext()) {
            final Entry<String, JsonElement> entry = iterator.next();
            String value;

            try {
                value = entry.getValue().getAsString();
            } catch (final Exception e) {
                continue;
            }

            if (value != null && value.equalsIgnoreCase(event.getMessageId())) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onReaction(final GenericGuildMessageReactionEvent event) {
        if (this.plugin.ignoring.contains(event.getGuild().getId())) {
            return;
        }

        final Configuration cfg = this.plugin.getHilda().getConfigurationManager().getConfiguration(this.plugin, event.getGuild().getId());
        final TextChannel send = event.getGuild().getTextChannelById(cfg.getString("pinboard", "12345"));

        if (send == null) {
            Hilda.getLogger().fine("There is no reaction channel set");
            return;
        }

        if (!PinboardPlugin.EMOTE.equals(event.getReactionEmote().getName())) {
            Hilda.getLogger().fine(event.getReactionEmote().getName());
            return;
        }

        if (!send.canTalk()) {
            User owner = event.getGuild().getOwner().getUser();
            owner.openPrivateChannel().queue(pc -> {
                MessageBuilder mb = new MessageBuilder();
                mb.append("Hey, ").append(owner.getName()).append("! You currently have the pinboard on ");
                mb.append(event.getGuild().getName()).append(" set to output to ").append(send).append("! ");
                mb.append("Unfortunately I can't send messages to that channel. To fix this, give me permission to send ");
                mb.append("messages to that channel, change the channel I should send messages to with ");
                mb.append(CommandManager.PREFIX + "pinboard channel <#channel>", MessageBuilder.Formatting.BOLD).append(" or ");
                mb.append("disable the pinboard with ").append(CommandManager.PREFIX + "pinboard disable", MessageBuilder.Formatting.BOLD);
                mb.append(".");
                pc.sendMessage(mb.build());
            });
            return;
        }

        Hilda.getLogger().fine("Handing over a reaction");
        this.plugin.executor.execute(new PinboardTask(this.hilda, event, cfg));
    }

}
