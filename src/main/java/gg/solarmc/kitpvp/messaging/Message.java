package gg.solarmc.kitpvp.messaging;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Message {

    public Message(List<String> strings, List<Parser> parsers) {
        this.strings = strings;
        this.parserList = parsers;
    }

    private final List<String> strings;
    private final List<Parser> parserList;

    public void target(Player player) {

        Stream<String> stream = strings.stream();

        for (Parser parser : parserList) {
            stream = stream.map(parser);
        }

        List<String> strings = stream.collect(Collectors.toList());

        for (String parsed : strings) {
            Prefix.message(player,parsed);
        }

    }


}
