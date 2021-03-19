package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpConfig;
import gg.solarmc.kitpvp.KitpvpPlugin;
import gg.solarmc.kitpvp.messaging.MessageConfig;
import gg.solarmc.loader.DataCenter;

//i wish i used dependency injection... ;-;
public class KillModule {

    private final KitpvpPlugin plugin;
    private final DamageMap map;
    private final KillDataHandler killDataHandler;
    private final DamageListener listener;


    public KillModule(KitpvpPlugin plugin, KitpvpConfig config, MessageConfig messageConfig, DataCenter center) {
        this.plugin = plugin;
        this.map = new DamageMap(plugin);
        this.killDataHandler = new KillDataHandler(center, config);
        this.listener = new DamageListener(map,killDataHandler,messageConfig,config);
    }

    public KillModule initializeListeners() {
        plugin.getServer().getPluginManager().registerEvents(listener,plugin);

        return this;
    }
}
