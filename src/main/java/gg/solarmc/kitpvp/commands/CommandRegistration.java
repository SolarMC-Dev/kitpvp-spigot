package gg.solarmc.kitpvp.commands;

import gg.solarmc.kitpvp.HasLifecycle;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.util.List;

@Singleton
public final class CommandRegistration implements HasLifecycle {

    private final Plugin plugin;
    private final List<Command> commands;

    public CommandRegistration(Plugin plugin, List<Command> commands) {
        this.plugin = plugin;
        this.commands = List.copyOf(commands);
    }

    @Inject
    public CommandRegistration(Plugin plugin, BountyCommand bountyCommand, AdminCommand reloadCommand) {
        this(plugin, List.of(bountyCommand, reloadCommand));
    }

    @Override
    public void start() {
        plugin.getServer().getCommandMap().registerAll("kitpvp", commands);
    }

    @Override
    public void reload() {

    }

    @Override
    public void stop() {
        CommandMap commandMap = plugin.getServer().getCommandMap();
        commandMap.getKnownCommands().values().removeAll(commands);
    }
}
