package gg.solarmc.kitpvp.messaging.parsers;

import gg.solarmc.kitpvp.messaging.Parser;
import org.bukkit.entity.Player;

/**
 * Represents something that happens to one player
 */
public class SinglePlayerParser implements Parser {

    public SinglePlayerParser(Player occurer) {
        this.occurer = occurer;
    }

    private final Player occurer;

    @Override
    public String apply(String s) {
        return s.replaceAll("%PLAYER%",occurer.getDisplayName());
    }
}
