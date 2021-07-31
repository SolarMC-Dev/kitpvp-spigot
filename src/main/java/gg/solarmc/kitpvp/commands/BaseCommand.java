package gg.solarmc.kitpvp.commands;

import gg.solarmc.kitpvp.config.Config;
import gg.solarmc.kitpvp.config.ConfigCenter;
import org.bukkit.command.CommandSender;
import space.arim.api.env.bukkit.BukkitCommandSkeleton;

import java.util.List;

public abstract class BaseCommand extends BukkitCommandSkeleton {

    private final ConfigCenter configCenter;

    public BaseCommand(String command, ConfigCenter configCenter) {
        super(command);
        this.configCenter = configCenter;
    }

    public final ConfigCenter configCenter() {
        return configCenter;
    }

    public final Config config() {
        return configCenter().config();
    }

    public abstract void permission(StringBuilder builder);

    public abstract void execute(CommandSender sender, CommandIterator command);

    @Override
    protected void execute(CommandSender sender, String[] args) {
        StringBuilder permissionBuilder = new StringBuilder("solar.kitpvp.command.");
        permission(permissionBuilder);
        if (!sender.hasPermission(permissionBuilder.toString())) {
            sender.sendMessage(config().messages().noPermission());
            return;
        }
        execute(sender, new CommandIterator(args));
    }

    @Override
    protected List<String> suggest(CommandSender sender, String[] args) {
        return List.of();
    }
}
