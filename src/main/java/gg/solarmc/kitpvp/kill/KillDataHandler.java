package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpConfig;
import gg.solarmc.kitpvp.kill.levelling.LevelUtil;
import gg.solarmc.kitpvp.kill.result.KillResult;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import gg.solarmc.loader.kitpvp.OnlineKitPvp;
import gg.solarmc.loader.kitpvp.StatisticResult;
import org.bukkit.entity.Player;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.math.BigDecimal;
import java.util.Set;

public class KillDataHandler {

    private final DataCenter center;
    private final KitpvpConfig config;

    public KillDataHandler(DataCenter center, KitpvpConfig config) {
        this.center = center;
        this.config = config;
    }

    CentralisedFuture<KillResult> handleKill(Player killer, Player killed, Set<Player> assisters) {
        return center.transact(transaction -> {
            OnlineKitPvp killerData = killer.getSolarPlayer().getData(KitPvpKey.INSTANCE);
            OnlineKitPvp killedData = killed.getSolarPlayer().getData(KitPvpKey.INSTANCE);

            //modify killer
            killerData.addKills(transaction,1);
            int newStreak = killerData.addKillstreaks(transaction,1).newValue();

            StatisticResult exp = killer.getSolarPlayer().getData(KitPvpKey.INSTANCE).addExperience(transaction,1);

            //modify killed
            killedData.addDeaths(transaction,1);
            int old = killedData.resetCurrentKillstreaks(transaction);


            //add assists
            for (Player assister : assisters) {
                assister.getSolarPlayer().getData(KitPvpKey.INSTANCE).addAssists(transaction,1);
                assister.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction, BigDecimal.valueOf(config.assistMoney()));
            }

            int rewardAmount = calcStreakReward(config.killMoney(), config.killAdditionInterval(), config.killAddition(), newStreak);

            killer.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction, BigDecimal.valueOf(rewardAmount));

            return new KillResult(LevelUtil.isLevelUp(exp.oldValue(),exp.newValue()), old, newStreak, rewardAmount, config.assistMoney());
        });
    }

    CentralisedFuture<?> handleDeath(Player killed, Set<Player> assisters) {
        return center.runTransact(transaction -> {
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).addDeaths(transaction,1);
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).resetCurrentKillstreaks(transaction);

            //add assists
            for (Player assister : assisters) {
                assister.getSolarPlayer().getData(KitPvpKey.INSTANCE).addAssists(transaction,1);
                assister.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction, BigDecimal.valueOf(config.assistMoney()));
            }

            //reset killed's killstreak
        });
    }

    private int calcStreakReward(int base, int interval, int addition, int currentStreak) {
        if (currentStreak > interval) {
            int rewardTier = (currentStreak / interval) * addition;

            base = base + rewardTier;
        }

        return base;
    }



}
