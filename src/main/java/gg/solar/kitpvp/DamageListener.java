package gg.solar.kitpvp;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DamageListener implements Listener {

    private final KitpvpPlugin plugin;
    private final DataHandler dataHandler;
    private final DamageMap damageMap;
    private final RewardHandler rewardHandler;

    public DamageListener(KitpvpPlugin plugin, DataHandler dataHandler, DamageMap damageMap, RewardHandler rewardHandler) {
        this.plugin = plugin;
        this.dataHandler = dataHandler;
        this.damageMap = damageMap;
        this.rewardHandler = rewardHandler;
    }

    @EventHandler //honk
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {

            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            damageMap.trackDamage(damager,damaged);
        }
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = killed.getKiller();

        //probably a less lazy way to do this but my brain is cheese right now
        if (killer != null) {
            dataHandler.incrementPlayerKill(killer,1);
            dataHandler.incrementPlayerAssists(damageMap.getData(killed).getAssists(killer),1);
        } else {
            dataHandler.incrementPlayerAssists(damageMap.getData(killed).getDamagers(),1);
        }

        damageMap.wipe(killed);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        //better safe than sorry
        damageMap.wipe(event.getPlayer());
    }

}
