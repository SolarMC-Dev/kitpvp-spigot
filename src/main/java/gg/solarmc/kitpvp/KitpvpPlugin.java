package gg.solarmc.kitpvp;

import gg.solarmc.kitpvp.kill.DamageMap;
import gg.solarmc.kitpvp.kill.KillModule;
import gg.solarmc.kitpvp.kill.levelling.LevelConfig;
import gg.solarmc.kitpvp.messaging.MessageConfig;
import gg.solarmc.kitpvp.stat.PlaceholderAPIHook;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.helper.ConfigurationHelper;

//TODO decoupling
public class KitpvpPlugin extends JavaPlugin {
    private Configs configs;

    private KillModule module;

    private final DamageMap damageMap = new DamageMap(this);


    @Override
    public void onEnable() {
       this.configs = new Configs(
               new ConfigurationHelper<>(this.getDataFolder().toPath(), "config.yml",
               new SnakeYamlConfigurationFactory<>(KitpvpConfig.class, ConfigurationOptions.defaults())),

               new ConfigurationHelper<>(this.getDataFolder().toPath(), "messages.yml",
                       new SnakeYamlConfigurationFactory<>(MessageConfig.class, ConfigurationOptions.defaults())),

               new ConfigurationHelper<>(this.getDataFolder().toPath(), "levels.yml",
                       new SnakeYamlConfigurationFactory<>(LevelConfig.class, ConfigurationOptions.defaults()))
       );

       configs.load();

       module = new KillModule(this, configs.getConfig(), configs.getMessageConfig(), getServer().getDataCenter()).initializeListeners();

       //papi shit
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }
    }




}
