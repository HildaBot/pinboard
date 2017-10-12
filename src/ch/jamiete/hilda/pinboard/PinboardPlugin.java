package ch.jamiete.hilda.pinboard;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.pinboard.commands.PinboardBaseCommand;
import ch.jamiete.hilda.plugins.HildaPlugin;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class PinboardPlugin extends HildaPlugin {
    public static final String EMOTE = "\u2B50";
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    public final List<String> ignoring = new ArrayList<>();

    public PinboardPlugin(final Hilda hilda) {
        super(hilda);
    }

    @Override
    public void onEnable() {
        this.getHilda().getBot().addEventListener(new PinboardListener(this.getHilda(), this));
        this.getHilda().getCommandManager().registerChannelCommand(new PinboardBaseCommand(this.getHilda(), this));
    }

    public void onDisable() {
        this.executor.shutdown();

        try {
            this.executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            Hilda.getLogger().log(Level.WARNING, "Encountered exception whilst terminating pinboard executor", e);
        }
    }

}
