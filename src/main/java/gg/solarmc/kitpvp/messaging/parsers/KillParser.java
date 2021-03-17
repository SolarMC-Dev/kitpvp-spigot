package gg.solarmc.kitpvp.messaging.parsers;

import gg.solarmc.kitpvp.messaging.Parser;
import org.bukkit.entity.Player;

public class KillParser implements Parser {

    private final Player killer;
    private final Player killed;

    public KillParser(Player killer, Player killed) {
        this.killer = killer;
        this.killed = killed;
    }

    @Override
    public String apply(String s) {
        return s.replaceAll("%KILLER%",killer.getDisplayName())
                .replaceAll("%KILLED%",killed.getDisplayName());
    }
}
