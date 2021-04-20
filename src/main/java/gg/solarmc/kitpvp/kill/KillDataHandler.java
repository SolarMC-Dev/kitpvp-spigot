package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpConfig;
import gg.solarmc.kitpvp.kill.result.KillResult;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.credits.CreditsKey;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import org.bukkit.entity.Player;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.math.BigDecimal;
import java.util.Collection;
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
            killer.getSolarPlayer().getData(KitPvpKey.INSTANCE).addKills(transaction,1);
            killer.getSolarPlayer().getData(KitPvpKey.INSTANCE).addKillstreaks(transaction,1);
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).addDeaths(transaction,1);
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).resetCurrentKillstreaks(transaction);
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).addExperience(transaction,1);

            //add assists
            for (Player assister : assisters) {
                assister.getSolarPlayer().getData(KitPvpKey.INSTANCE).addAssists(transaction,1);
                killer.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction, BigDecimal.valueOf(config.assistMoney()));
            }

            //increment killer's killstreak
            //reset killed's killstreak

            killer.getSolarPlayer().getData(CreditsKey.INSTANCE).depositBalance(transaction, BigDecimal.valueOf(config.killMoney()));

            return new KillResult(false);
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



}
