package ch.jamiete.hilda.pinboard.commands;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.pinboard.PinboardPlugin;
import net.dv8tion.jda.core.Permission;

public class PinboardBaseCommand extends ChannelSeniorCommand {

    public PinboardBaseCommand(final Hilda hilda, final PinboardPlugin plugin) {
        super(hilda);

        this.setName("pinboard");
        this.setDescription("Manages the pinboard plugin.");
        this.setMinimumPermission(Permission.MANAGE_SERVER);

        this.registerSubcommand(new PinboardChannelCommand(hilda, this, plugin));
        this.registerSubcommand(new PinboardImportCommand(hilda, this, plugin));
    }

}
