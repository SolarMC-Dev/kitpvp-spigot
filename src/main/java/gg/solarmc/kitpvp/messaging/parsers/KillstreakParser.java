package gg.solarmc.kitpvp.messaging.parsers;

import gg.solarmc.kitpvp.messaging.Parser;

public class KillstreakParser implements Parser {

    private final String streakAmount;

    public KillstreakParser(int amount) {
        this.streakAmount = amount + "";
    }

    public KillstreakParser(String streakAmount) {
        this.streakAmount = streakAmount;
    }

    @Override
    public String apply(String s) {
        return s.replaceAll("%STREAK%",streakAmount);
    }
}
