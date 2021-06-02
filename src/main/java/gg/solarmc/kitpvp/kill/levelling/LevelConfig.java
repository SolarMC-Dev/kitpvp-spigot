package gg.solarmc.kitpvp.kill.levelling;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.ConfSerialisers;

import java.util.List;
@ConfSerialisers(LevelColorSerializer.class)
public interface LevelConfig {

    @ConfKey("level_messages.levels")
    @ConfDefault.DefaultStrings({
            "0-10-&7",
            "11-20-&a",
            "21-30-&e"
    })
    List<LevelColor> getLevelPrefixes();

    @ConfKey("level_messages.fallback")
    @ConfDefault.DefaultString("0-0-&7")
    LevelColor getFallbackPrefix();

}
