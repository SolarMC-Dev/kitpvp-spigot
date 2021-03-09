package gg.solar.kitpvp;

import me.aurium.beetle.core.command.AbstractCommand;
import me.aurium.beetle.spigot.command.SpigotCommandData;
import org.bukkit.entity.Player;

/**
 * TODO replace this with a BranchCommand from Branch Framework
 */
public class StatsCommand implements AbstractCommand<SpigotCommandData> {
    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getPermission() {
        return "kitpvp.stats";
    }

    @Override
    public boolean execute(SpigotCommandData spigotCommandData, String[] args) {

        if (!(spigotCommandData.getSender() instanceof Player)) {
            spigotCommandData.debugMessage("You cannot run this as console!");
            return true;
        }

        Player sender = (Player) spigotCommandData.getSender();

        if (args[1] != null) {
            sender = null; //getFromArgs
        }




        return false;
    }
}
