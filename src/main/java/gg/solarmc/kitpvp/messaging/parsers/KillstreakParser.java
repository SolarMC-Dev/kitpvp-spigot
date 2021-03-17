package gg.solarmc.kitpvp.messaging.parsers;

import gg.solarmc.kitpvp.messaging.Parser;

public class KillstreakParser implements Parser {

    public KillstreakParser(String streakAmount) {
        this.streakAmount = streakAmount;
    }

    private final String streakAmount;

    @Override
    public String apply(String s) {
        return s.replaceAll("%STREAK%",streakAmount);
    }
}
