package gg.solarmc.kitpvp;

import gg.solarmc.kitpvp.kill.KillModule;
import gg.solarmc.kitpvp.kill.damage.DamageMap;
import gg.solarmc.kitpvp.messaging.MessageConfig;
import gg.solarmc.kitpvp.stat.PlaceholderAPIHook;
import gg.solarmc.kitpvp.stat.StatCommand;
import me.aurium.beetle.spigot.SpigotBeetle;
import me.aurium.beetle.spigot.SpigotBeetleFactory;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.helper.ConfigurationHelper;

//TODO decoupling
public class KitpvpPlugin extends JavaPlugin {
    private Configs configs;

    private KillModule module;

    private final DamageMap damageMap = new DamageMap(this);
    private final SpigotBeetle spigotBeetle = new SpigotBeetleFactory(this,false).build();


    @Override
    public void onEnable() {
       this.configs = new Configs(
               new ConfigurationHelper<>(this.getDataFolder().toPath(), "config.yml",
               new SnakeYamlConfigurationFactory<>(KitpvpConfig.class, ConfigurationOptions.defaults())),

               new ConfigurationHelper<>(this.getDataFolder().toPath(), "config.yml",
                       new SnakeYamlConfigurationFactory<>(MessageConfig.class, ConfigurationOptions.defaults()))
               );

       configs.loadTry();

       module = new KillModule(this, configs.getConfig(), configs.getMessageConfig(), getServer().getDataCenter()).initializeListeners();

       //man why use a SimpleCommand when you can use branch... NOT! (too lazy to finish branch framework rn, i'll update when possible.)
       spigotBeetle.getCommandRegistry().registerCommand(new StatCommand(configs.getMessageConfig(), getServer().getDataCenter()));

       spigotBeetle.getCommandRegistry();

       //papi shit
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }
    }




}
