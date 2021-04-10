package gg.solarmc.kitpvp.messaging;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Message {

    public Message(List<String> strings, List<Parser> parsers) {
        this.strings = strings;
        this.parserList = parsers;
    }

    private final List<String> strings;
    private final List<Parser> parserList;

    public void target(Player player) {

        List<String> modified = new ArrayList<>();

        for (String string : strings) {
            String moddy = string;

            for (Parser parser : parserList) {
                moddy = parser.apply(moddy);
            }

            modified.add(moddy);
        }

        for (String parsed : modified) {
            Prefix.message(player,parsed);
        }

    }


}
