package ch.jamiete.hilda.pinboard.commands;

import java.util.Arrays;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.pinboard.PinboardPlugin;
import net.dv8tion.jda.api.Permission;

public class PinboardBaseCommand extends ChannelSeniorCommand {

    public PinboardBaseCommand(final Hilda hilda, final PinboardPlugin plugin) {
        super(hilda);

        this.setName("pinboard");
        this.setAliases(Arrays.asList("starboard"));
        this.setDescription("Manages the pinboard plugin.");
        this.setMinimumPermission(Permission.MANAGE_SERVER);

        this.registerSubcommand(new PinboardAgeCommand(hilda, this, plugin));
        this.registerSubcommand(new PinboardChannelCommand(hilda, this, plugin));
        this.registerSubcommand(new PinboardImportCommand(hilda, this, plugin));
        this.registerSubcommand(new PinboardMinimumCommand(hilda, this, plugin));
        this.registerSubcommand(new PinboardToggleCommand(hilda, this, plugin));
    }

}
