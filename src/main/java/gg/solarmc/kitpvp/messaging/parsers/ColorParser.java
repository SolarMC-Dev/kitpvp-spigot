package gg.solarmc.kitpvp.messaging.parsers;

import gg.solarmc.kitpvp.messaging.Parser;
import org.bukkit.ChatColor;

public class ColorParser implements Parser {

    @Override
    public String apply(String s) {
        return ChatColor.translateAlternateColorCodes('&',s);
    }

}
