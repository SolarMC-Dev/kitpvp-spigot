package gg.solarmc.kitpvp.stat;

import me.aurium.beetle.api.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

//simple command because i cannot be arsed to finish Branch Framework right now
public class StatCommand implements Command<CommandSender> {
    @Override
    public String getName() {
        return "kitpvp";
    }

    @Override
    public String getPermission() {
        return "kitpvp.command";
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {

        if (strings.length == 0) {
            noArgs();

            return true;
        }

        switch (strings[0]) {
            case "stats":

                return true;
            case "idk":

            default:
                noArgs();
        }

        return false;
    }

    //this is so cringe tbh just use branch
    @Override
    public Collection<String> tabComplete(CommandSender sender, String alias, String[] args) {
        return Set.of("stats","idk");
    }

    void noArgs() {

    }
}
