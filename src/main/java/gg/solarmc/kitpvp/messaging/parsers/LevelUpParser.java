package gg.solarmc.kitpvp.messaging.parsers;

import gg.solarmc.kitpvp.messaging.Parser;

public class LevelUpParser implements Parser {

    public LevelUpParser(double newLevel, int totalXP) {
        this.newLevel = newLevel + "";
        this.totalXP = totalXP + "";
    }

    public LevelUpParser(String newLevel, String totalXP) {
        this.newLevel = newLevel;
        this.totalXP = totalXP;
    }

    private final String newLevel;
    private final String totalXP;


    @Override
    public String apply(String s) {
        return s.replaceAll("%LEVEL%",newLevel).replaceAll("%TOTALXP%",totalXP);
    }
}
