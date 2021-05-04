package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpConfig;
import gg.solarmc.kitpvp.KitpvpPlugin;
import gg.solarmc.kitpvp.messaging.MessageConfig;
import gg.solarmc.loader.DataCenter;

//i wish i used dependency injection... ;-;
public class KillModule {

    private final KitpvpPlugin plugin;
    private final DamageListener listener;

    public KillModule(KitpvpPlugin plugin, KitpvpConfig config, MessageConfig messageConfig, DataCenter center) {
        this.plugin = plugin;
        DamageMap map = new DamageMap(plugin);
        KillDataHandler killDataHandler = new KillDataHandler(center, config); //TOOD un-local these if we need access to them
        this.listener = new DamageListener(plugin.getServer(), map, killDataHandler,messageConfig);
    }

    public KillModule initializeListeners() {
        plugin.getServer().getPluginManager().registerEvents(listener,plugin);

        return this;
    }
}
