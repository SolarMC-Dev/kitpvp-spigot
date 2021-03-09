package gg.solar.kitpvp;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

public interface MessageConfig {

    @ConfKey("messages.level_up")
    @ConfDefault.DefaultString("Level Up! You are now level %s")
    String getLevelUpMessage();

    @ConfKey("messages.killstreak_reached")
    @ConfDefault.DefaultString("%s has reached a killstreak of %s!")
    String getKillstreakReachedMessage();

    @ConfKey("messages.killstreak_ended")
    @ConfDefault.DefaultString("%s has ended %s's killstreak of %s!")
    String getKillstreakEndedMessage();

}
