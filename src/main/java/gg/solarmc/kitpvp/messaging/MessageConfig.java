package gg.solarmc.kitpvp.messaging;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

import java.util.List;

public interface MessageConfig {

    @ConfKey("messages.level.level_up")
    @ConfDefault.DefaultStrings({
            "[CHAT]You levelled up to level %LEVEL% with xp %TOTALXP%",
            "[TITLE]Level Up!"
    })
    List<String> getLevelUpActions();

    @ConfKey("messages.killstreak.streak_reached")
    @ConfDefault.DefaultStrings("[BROADCAST]%PLAYER% has reached a killstreak of %STREAK%!")
    List<String> getKillstreakReachedMessage();

    @ConfKey("messages.killstreak.streak_ended")
    @ConfDefault.DefaultStrings("[BROADCAST]%PLAYER1% has ended %PLAYER2%'s killstreak of %STREAK%!")
    List<String> getKillstreakEndedMessage();

    @ConfKey("messages.killstreak.streak_stoped")
    @ConfDefault.DefaultStrings("[BROADCAST]%PLAYER1% has lost their killstreak of %STREAK%!")
    List<String> getKillstreakStoppedMessage();

    @ConfDefault.DefaultStrings("[ACTIONBAR]You received %MONEY%$")
    @ConfKey("message.money.received")
    List<String> getReceiveMoneyMessage();

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
