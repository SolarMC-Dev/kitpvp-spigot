package gg.solarmc.kitpvp.kill;

import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import org.bukkit.entity.Player;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Collection;
import java.util.Set;

public class KillDataHandler {

    public KillDataHandler(DataCenter center) {
        this.center = center;
    }

    private final DataCenter center;

    CentralisedFuture<?> handleKill(Player killer, Player killed, Set<Player> assisters) {
        return center.runTransact(transaction -> {
            killer.getSolarPlayer().getData(KitPvpKey.INSTANCE).addKills(transaction,1);
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).addDeaths(transaction,1);

            //add assists
            for (Player assister : assisters) {
                assister.getSolarPlayer().getData(KitPvpKey.INSTANCE).addAssists(transaction,1);
            }

            //increment killer's killstreak
            //reset killed's killstreak
        });
    }

    CentralisedFuture<?> handleDeath(Player killed, Set<Player> assisters) {
        return center.runTransact(transaction -> {
            killed.getSolarPlayer().getData(KitPvpKey.INSTANCE).addDeaths(transaction,1);

            //add assists
            for (Player assister : assisters) {
                assister.getSolarPlayer().getData(KitPvpKey.INSTANCE).addAssists(transaction,1);
            }

            //reset killed's killstreak
        });
    }



}
