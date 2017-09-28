package ch.jamiete.hilda.pinboard;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.pinboard.commands.PinboardBaseCommand;
import ch.jamiete.hilda.plugins.HildaPlugin;

public class PinboardPlugin extends HildaPlugin {
    public static final int REQUIRED = 3;
    public static final String EMOTE = "\u2B50";

    public PinboardPlugin(final Hilda hilda) {
        super(hilda);
    }

    @Override
    public void onEnable() {
        this.getHilda().getBot().addEventListener(new PinboardListener(this));
        this.getHilda().getCommandManager().registerChannelCommand(new PinboardBaseCommand(this.getHilda(), this));
    }

}
