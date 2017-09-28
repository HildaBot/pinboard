package ch.jamiete.hilda.pinboard.commands;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.pinboard.PinboardPlugin;
import ch.jamiete.hilda.pinboard.PinboardUtil;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class PinboardImportCommand extends ChannelSubCommand {
    private final PinboardPlugin plugin;

    protected PinboardImportCommand(final Hilda hilda, final ChannelSeniorCommand senior, final PinboardPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("import");
        this.setDescription("Imports the pinned messages from the current channel to the pinboard.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        final Configuration cfg = this.hilda.getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());
        final TextChannel send = message.getGuild().getTextChannelById(cfg.getString("pinboard", "12345"));

        if (send == null) {
            this.reply(message, "You must first set the pinboard channel.");
            return;
        }

        this.reply(message, "OK, I've started processing pinned messages in this channel.");

        message.getChannel().getPinnedMessages().queue(messages -> {
            for (final Message m : messages) {
                final String entryid = cfg.getString(m.getId(), null);

                if (entryid == null) {
                    send.sendMessage(PinboardUtil.build(1, m.getTextChannel(), m)).queue(sent -> {
                        cfg.setString(m.getId(), sent.getId());
                    });
                }
            }
        });
    }

}
