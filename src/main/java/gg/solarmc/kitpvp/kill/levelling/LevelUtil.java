package gg.solarmc.kitpvp.kill.levelling;

public class LevelUtil {

    public static double getLevel(int exp) {
        return Math.pow(0.5 + (2 * exp + 0.25),0.5);
    }

    public static boolean isLevelUp(int previousExp, int newExp) {
        return getLevel(previousExp) < getLevel(newExp);
    }



}
