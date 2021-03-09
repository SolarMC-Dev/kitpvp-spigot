package gg.solar.kitpvp;

import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.SolarPlayer;
import gg.solarmc.loader.impl.player.OnlineSolarPlayerImpl;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import org.bukkit.entity.Player;

public class KillHandler {

    private DataCenter dataCenter;

    /**
     * Handles death with a killer
     * @param killer
     */
    public void handleKill(Player killer) {
        dataCenter.runTransact(transaction -> {

            killer.getSolarPlayer().getData(KitPvpKey.INSTANCE).addKills(transaction,1);

            //increment kills
            //increment killstreak
            //increment experience, the tell the player if they have leveled up or not!
        });

        //broadcast
    }

    public void handleAssist(Player assist) {
        dataCenter.runTransact(transaction -> {
            assist.getSolarPlayer().getData(KitPvpKey.INSTANCE).addAssists(transaction,1);
        });
    }

    public void handleAssist(Iterable<Player> assisters) {
        dataCenter.runTransact(transaction -> {

            assisters.forEach(player -> {
                player.getSolarPlayer().getData(KitPvpKey.INSTANCE).addAssists(transaction,1);
            });

        });
    }

    /**
     * Handles death without a killer
     * @param killed
     */
    public void handleDeath(Player killed) {
        dataCenter.runTransact(transaction -> {
            //increment death
            //clear killstreak
        });
    }


}
