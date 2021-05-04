package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpConfig;
import gg.solarmc.kitpvp.kill.levelling.LevelUtil;
import gg.solarmc.kitpvp.messaging.MessageConfig;
import gg.solarmc.kitpvp.messaging.MessageController;
import gg.solarmc.kitpvp.messaging.parsers.*;
import gg.solarmc.kitpvp.util.Logging;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import gg.solarmc.loader.kitpvp.OnlineKitPvp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

public class DamageListener implements Listener {

    private final DamageMap damageMap;
    private final KillDataHandler killDataHandler;
    private final MessageConfig messageConfig;
    private final KitpvpConfig config;

    public DamageListener(DamageMap damageMap, KillDataHandler killDataHandler, MessageConfig messageConfig, KitpvpConfig config) {
        this.damageMap = damageMap;
        this.killDataHandler = killDataHandler;
        this.messageConfig = messageConfig;
        this.config = config;
    }

    @EventHandler //honk
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player damaged && event.getDamager() instanceof Player damager) {
            damageMap.trackDamage(damager,damaged);
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        if (killer != null) {
            Set<Player> immutableAssisters = damageMap.getHolder(killed).getAssists(killer);

            killDataHandler
                    .handleKill(killer,killed,immutableAssisters)
                    .thenAcceptSync(result -> {

                        PairPlayerParser pair = new PairPlayerParser(killer,killed);
                        SinglePlayerParser single = new SinglePlayerParser(killer);

                        //basic kill/death messages
                        MessageController.killType(messageConfig.getKillMessageKiller(),pair).target(killer);
                        MessageController.killType(messageConfig.getKillMessageKilled(),pair).target(killed);

                        //levelup for killer
                        if (result.isLevelUp()) {
                            OnlineKitPvp killerData = killer.getSolarPlayer().getData(KitPvpKey.INSTANCE);

                            LevelUpParser level = new LevelUpParser(LevelUtil.getLevel(killerData.currentExperience()),killerData.currentAssists());

                            MessageController.levelType(messageConfig.getLevelUpActions(),single,level).target(killer);
                        }

                        KillstreakParser killerStreak = new KillstreakParser(result.getKillerKillstreak());
                        KillstreakParser killedStreak = new KillstreakParser(result.getKilledKillstreak());

                        //killstreak messages
                        if (result.isEndedKillstreak()) {
                            MessageController.killstreakEndType(messageConfig.getKillstreakEndedMessage(),pair,killedStreak).target(killer);
                        }

                        if (result.getKillerKillstreak() % 10 == 0) {
                            MessageController.killstreakType(messageConfig.getKillstreakReachedMessage(),single,killerStreak).target(killer);
                        }

                        MoneyParser moneyParser = new MoneyParser(result.getRewardAmount());
                        MessageController.moneyType(messageConfig.getReceiveMoneyMessage(),moneyParser).target(killer);

                        //assister messages
                        for (Player player : immutableAssisters) {
                            MoneyParser assistParser = new MoneyParser(result.getAssistAmount());
                            MessageController.killType(messageConfig.getKillMessageAssist(),pair).target(player);
                            MessageController.moneyType(messageConfig.getReceiveMoneyMessage(),assistParser).target(player);

                        }
                    })
                    .whenComplete(Logging.INSTANCE);

        } else {
            Set<Player> immutableAssisters = damageMap.getHolder(killed).getDamagers();

            killDataHandler
                    .handleDeath(killed,immutableAssisters)
                    .thenRunSync(() -> {
                        SinglePlayerParser parser = new SinglePlayerParser(killed);
                        MessageController.deathType(messageConfig.getDeathMessageKilled(),parser).target(killed);

                        for (Player player : immutableAssisters) {
                            MessageController.deathType(messageConfig.getDeathMessageAssist(),parser).target(player);
                        }
                    })
                    .whenComplete(Logging.INSTANCE);
        }




    }


    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        damageMap.close(event.getPlayer());
    }

}
