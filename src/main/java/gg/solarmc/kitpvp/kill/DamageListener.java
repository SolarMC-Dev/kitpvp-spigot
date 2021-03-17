package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.kill.damage.DamageMap;
import gg.solarmc.kitpvp.messaging.MessageConfig;
import gg.solarmc.kitpvp.messaging.MessageController;
import gg.solarmc.kitpvp.messaging.parsers.PairPlayerParser;
import gg.solarmc.kitpvp.messaging.parsers.SinglePlayerParser;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.Optional;
import java.util.Set;

public class DamageListener implements Listener {

    private final DamageMap damageMap;
    private final KillDataHandler killDataHandler;

    private final MessageConfig messageConfig;


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

        if (killer != null) {
            Set<Player> immutableAssisters = damageMap.getHolder(killed).getAssists(killer);

            killDataHandler.handleKill(killer,killed,immutableAssisters);

            PairPlayerParser parser = new PairPlayerParser(killer,killed);

            MessageController.killType(messageConfig.getKillMessageKiller(),parser).target(killer);
            MessageController.killType(messageConfig.getKillMessageKilled(),parser).target(killed);

            for (Player player : immutableAssisters) {
                MessageController.killType(messageConfig.getKillMessageAssist(),parser).target(player);
            }
        } else {
            Set<Player> immutableAssisters = damageMap.getHolder(killed).getDamagers();

            killDataHandler.handleDeath(killed,immutableAssisters);

            SinglePlayerParser parser = new SinglePlayerParser(killed);

            MessageController.deathType(messageConfig.getDeathMessageKilled(),parser).target(killed);

            for (Player player : immutableAssisters) {
                MessageController.deathType(messageConfig.getDeathMessageAssist(),parser).target(player);
            }
        }




    }


    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        damageMap.wipeHolder(event.getPlayer());
    }

}
