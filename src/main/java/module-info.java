import gg.solarmc.kitpvp.provider.KitpvpPluginProvider;
import org.bukkit.plugin.LaunchablePlugin;

module gg.solarmc.kitpvp {
    requires com.github.benmanes.caffeine;
    requires gg.solarmc.loader;
    requires gg.solarmc.loader.credits;
    requires gg.solarmc.loader.kitpvp;
    requires gg.solarmc.paper.itemserializer;
    requires jakarta.inject;
    requires me.clip.placeholderapi;
    requires org.bukkit;
    requires org.slf4j;
    requires space.arim.api.env.bukkit;
    requires space.arim.api.jsonchat;
    requires space.arim.api.util.dazzleconf;
    requires space.arim.dazzleconf;
    requires space.arim.dazzleconf.ext.snakeyaml;
    requires space.arim.injector;
    exports gg.solarmc.kitpvp to space.arim.injector;
    exports gg.solarmc.kitpvp.commands to space.arim.injector;
    exports gg.solarmc.kitpvp.config to space.arim.injector;
    exports gg.solarmc.kitpvp.handler to space.arim.injector;
    exports gg.solarmc.kitpvp.listeners to space.arim.injector;
    exports gg.solarmc.kitpvp.misc to space.arim.injector;
    exports gg.solarmc.kitpvp.provider;
    opens gg.solarmc.kitpvp.listeners to org.bukkit;
    opens gg.solarmc.kitpvp.config to space.arim.dazzleconf;
    provides LaunchablePlugin with KitpvpPluginProvider;
}