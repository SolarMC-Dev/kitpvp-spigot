package gg.solarmc.kitpvp.kill.levelling;

public class LevelColor {

    private final int lower;
    private final int upper;
    private final String string;

    public LevelColor(int lower, int upper, String string) {
        this.lower = lower;
        this.upper = upper;
        this.string = string;
    }


    String getColoredString() {
        return string;
    }

    int getUpperBound() {
        return upper;
    }

    int getLowerBound() {
        return lower;
    }


}
