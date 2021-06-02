package gg.solarmc.kitpvp.kill.levelling;

import java.util.Optional;

public class ColorMap {

    private final LevelConfig config;

    public ColorMap(LevelConfig config) {
        this.config = config;
    }

    public LevelColor get(int level) {

        for (LevelColor color : config.getLevelPrefixes()) {
            if (level >= color.getLowerBound() && level < color.getUpperBound()) {
                return color;
            }
        }

        return config.getFallbackPrefix();
    }
}
