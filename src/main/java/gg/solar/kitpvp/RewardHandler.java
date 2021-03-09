package gg.solar.kitpvp;

import gg.solarmc.loader.DataCenter;
import org.bukkit.entity.Player;

public class RewardHandler {

    private DataCenter dataCenter;

    public void rewardKill(Player killer) {


        //tell them they got money!
    }

    public void rewardAssist(Player assister) {
        dataCenter.runTransact(transaction -> {
            //give money;
        });

        //tell them they got money!
    }

}
