package gg.solarmc.kitpvp.messaging.parsers;

import gg.solarmc.kitpvp.messaging.Parser;

public class StatParser implements Parser {

    public StatParser(int kills, int deaths, int assists, int experience, int current_killstreak, int highest_killstreak, int level) {
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.experience = experience;
        this.current_killstreak = current_killstreak;
        this.highest_killstreak = highest_killstreak;
        this.level = level;
    }

    private final int kills;
    private final int deaths;
    private final int assists;
    private final int experience;
    private final int current_killstreak;
    private final int highest_killstreak;

    private final int level;


    @Override
    public String apply(String s) {
        return s
                .replaceAll("%KILLS%", String.valueOf(kills))
                .replaceAll("%DEATHS%", String.valueOf(deaths))
                .replaceAll("%ASSISTS%", String.valueOf(assists))
                .replaceAll("%EXP%",String.valueOf(experience))
                .replaceAll("%CUR_KILLSTREAK%",String.valueOf(current_killstreak))
                .replaceAll("%HIGH_KILLSTREAK%",String.valueOf(highest_killstreak))
                .replaceAll("%LEVEL%",String.valueOf(level));
    }
}
