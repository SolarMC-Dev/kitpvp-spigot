package gg.solarmc.kitpvp.messaging;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

import java.util.List;

public interface MessageConfig {

    @ConfKey("messages.level.level_up")
    @ConfDefault.DefaultStrings({"[CHAT]You levelled up to level %LEVEL% with xp %TOTALXP%"})
    List<String> getLevelUpActions();

    @ConfKey("messages.killstreak.streak_reached")
    @ConfDefault.DefaultStrings("[CHAT]%PLAYER% has reached a killstreak of %STREAK%!")
    List<String> getKillstreakReachedMessage();

    @ConfKey("messages.killstreak.streak_ended")
    @ConfDefault.DefaultStrings("[CHAT]%PLAYER1% has ended %PLAYER2%'s killstreak of %STREAK%!")
    List<String> getKillstreakEndedMessage();

    @ConfKey("messages.kill.killer_message")
    @ConfDefault.DefaultStrings("[CHAT]You killed %s!")
    List<String> getKillMessageKiller();

    @ConfKey("messages.kill.killed_message")
    @ConfDefault.DefaultStrings("[CHAT]%s killed you!")
    List<String> getKillMessageKilled();

    @ConfKey("messages.kill.assist_message")
    @ConfDefault.DefaultStrings("[CHAT]You helped %s kill %s!")
    List<String> getKillMessageAssist();

    @ConfKey("messages.death.killed_message")
    @ConfDefault.DefaultStrings("[CHAT]You died!")
    List<String> getDeathMessageKilled();

    @ConfKey("messages.kill.assist_message")
    @ConfDefault.DefaultStrings("[CHAT]You helped %s die!")
    List<String> getDeathMessageAssist();

    @ConfKey("messages.stats.stat_message")
    @ConfDefault.DefaultStrings({
            "[CHAT]Your kills: %KILLS%",
            "[CHAT]Your deaths: %DEATHS%",
            "[CHAT]Your exp: %EXP%",
            "[CHAT]Your assists: %ASSISTS%",
            "[CHAT]Your level: %LEVEL%",
            "[CHAT]Your KDA: %KDA%"
    })
    List<String> getStatMessage();

}
