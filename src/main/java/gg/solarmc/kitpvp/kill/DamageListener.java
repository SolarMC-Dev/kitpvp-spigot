package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.Util;
import gg.solarmc.kitpvp.messaging.MessageConfig;
import gg.solarmc.kitpvp.messaging.MessageController;
import gg.solarmc.kitpvp.messaging.parsers.PairPlayerParser;
import gg.solarmc.kitpvp.messaging.parsers.SinglePlayerParser;
import gg.solarmc.kitpvp.util.Logging;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

public class DamageListener implements Listener {

    private final Server server;
    private final DamageMap damageMap;
    private final KillDataHandler killDataHandler;
    private final MessageConfig messageConfig;

    public DamageListener(Server server, DamageMap damageMap, KillDataHandler killDataHandler, MessageConfig messageConfig) {
        this.server = server;
        this.damageMap = damageMap;
        this.killDataHandler = killDataHandler;
        this.messageConfig = messageConfig;
    }

    @EventHandler //honk
    public void onDamage(EntityDamageByEntityEvent event) {

        damageMap.trackDamage(event.getEntity().getUniqueId(),event.getDamager().getUniqueId());

    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        Player killer = event.getEntity().getKiller();

        UUID killedId = event.getEntity().getUniqueId();

        if (killer != null) {
            UUID killerId = killer.getUniqueId();

            killDataHandler.handleKill(killer,killed,immutableAssisters).whenComplete(Logging.INSTANCE);

            //messaging

            PairPlayerParser parser = new PairPlayerParser(killer,killed);
            MessageController.killType(messageConfig.getKillMessageKiller(),parser).target(killer);
            MessageController.killType(messageConfig.getKillMessageKilled(),parser).target(killed);

            for (UUID player : immutableAssisters) {
                MessageController.killType(messageConfig.getKillMessageAssist(),parser).target(player,server);
            }
        } else {
            Set<UUID> immutableAssisters = damageMap.getHolder(killedId).getDamagers();

            killDataHandler.handleDeath(killed,immutableAssisters).whenComplete(Logging.INSTANCE);
            //messaging

            SinglePlayerParser parser = new SinglePlayerParser(killed);
            MessageController.deathType(messageConfig.getDeathMessageKilled(),parser).target(killed);

            for (UUID player : immutableAssisters) {
                MessageController.deathType(messageConfig.getDeathMessageAssist(),parser).target(player,server);
            }
        }




    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        damageMap.close(event.getPlayer());
    }

}
