package ch.jamiete.hilda.pinboard.commands;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.configuration.Configuration;
import ch.jamiete.hilda.pinboard.PinboardPlugin;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class PinboardChannelCommand extends ChannelSubCommand {
    private final PinboardPlugin plugin;

    public PinboardChannelCommand(final Hilda hilda, final ChannelSeniorCommand senior, final PinboardPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("channel");
        this.setDescription("Sets the channel to output pins to. If you change this, I will forget all current pins.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        final Configuration cfg = this.hilda.getConfigurationManager().getConfiguration(this.plugin, message.getGuild().getId());

        if (arguments.length == 0) {
            final TextChannel channel = message.getGuild().getTextChannelById(cfg.getString("pinboard", "12345"));

            if (channel == null) {
                this.reply(message, "There is no channel currently set.");
            } else {
                this.reply(message, "I am currently outputting pins to " + channel.getAsMention() + "!");
            }

            return;
        }

        if (message.getMentionedChannels().isEmpty()) {
            this.reply(message, "You must mention the channel.");
            return;
        }

        cfg.reset();
        cfg.setString("pinboard", message.getMentionedChannels().get(0).getId());

        this.reply(message, "OK, I will now output pins to " + message.getMentionedChannels().get(0).getAsMention() + "! Any pins that existed have now been forgotten.");
    }

}
