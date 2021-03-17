package gg.solarmc.kitpvp.messaging.parsers;

import gg.solarmc.kitpvp.messaging.Parser;
import org.bukkit.entity.Player;

public class PairPlayerParser implements Parser {

    public PairPlayerParser(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    private final Player player1;
    private final Player player2;

    @Override
    public String apply(String s) {
        return s.replaceAll("%PLAYER1%",player1.getDisplayName())
                .replaceAll("%PLAYER2%",player2.getDisplayName());
    }
}
