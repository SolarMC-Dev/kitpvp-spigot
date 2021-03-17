package gg.solarmc.kitpvp.messaging;

import gg.solarmc.kitpvp.messaging.parsers.*;

import java.util.List;

public class MessageController {

    public static Message levelType(List<String> strings, SinglePlayerParser playerParser, LevelUpParser parser) {
        return new Message(
                strings,
                List.of(new ColorParser(),playerParser,parser)
        );
    }

    public static Message killstreakType(List<String> strings, SinglePlayerParser parser, KillstreakParser parser2) {
        return new Message(
                strings,
                List.of(new ColorParser(),parser,parser2)
        );
    }

    public static Message killType(List<String> message, PairPlayerParser parser) {
        return new Message(
                message,
                List.of(new ColorParser(),parser)
        );
    }

    public static Message deathType(List<String> message, SinglePlayerParser parser) {
        return new Message(message,List.of(new ColorParser(),parser));
    }





}
