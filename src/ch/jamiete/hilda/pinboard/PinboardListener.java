package ch.jamiete.hilda.pinboard;

import java.util.Iterator;
import java.util.Map.Entry;
import com.google.gson.JsonElement;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.events.EventHandler;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.react.GenericGuildMessageReactionEvent;

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

        if (send == null) {
            return;
        }

        final Iterator<Entry<String, JsonElement>> iterator = cfg.get().entrySet().iterator();

        while (iterator.hasNext()) {
            final Entry<String, JsonElement> entry = iterator.next();
            String value = null;

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

        Hilda.getLogger().fine("Handing over a reaction");
        this.plugin.executor.execute(new PinboardTask(this.hilda, event, cfg));
    }

}
