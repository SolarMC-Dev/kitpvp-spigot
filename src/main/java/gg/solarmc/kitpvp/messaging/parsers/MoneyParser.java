package gg.solarmc.kitpvp.messaging.parsers;

import gg.solarmc.kitpvp.messaging.Parser;

public class MoneyParser implements Parser {

    private final int money;

    public MoneyParser(int money) {
        this.money = money;
    }

    @Override
    public String apply(String s) {
        return s.replaceAll("%MONEY%",money + "");
    }
}
