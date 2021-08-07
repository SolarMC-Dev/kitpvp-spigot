/*
 * kitpvp
 * Copyright © 2021 SolarMC Developers
 *
 * kitpvp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kitpvp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with kitpvp. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Affero General Public License.
 */

package gg.solarmc.kitpvp.listeners;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import gg.solarmc.kitpvp.HasLifecycle;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.kitpvp.handler.KillHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Duration;
import java.util.Set;

@Singleton
public class KillListener implements Listener, HasLifecycle {

    private final ConfigCenter configCenter;
    private final KillHandler killHandler;

    private Cache<Player, DamageTracker> damageTrackers;

    @Inject
    public KillListener(ConfigCenter configCenter, KillHandler killHandler) {
        this.configCenter = configCenter;
        this.killHandler = killHandler;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent damageEvent) {
        if (damageEvent.getEntity() instanceof Player victim
                && damageEvent.getDamager() instanceof Player attacker) {
            var tracker = damageTrackers.get(victim, (v) -> new DamageTracker());
            tracker.lastDamager = attacker;
            tracker.assistants.put(attacker, Boolean.TRUE);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent deathEvent) {
        Player dead = deathEvent.getEntity();
        DamageTracker tracker = damageTrackers.asMap().remove(dead);
        if (tracker == null) {
            return;
        }
        Player killer = tracker.lastDamager;
        Set<Player> assistants = tracker.assistants.asMap().keySet();
        assistants.remove(killer);
        killHandler.onKill(dead, killer, assistants);
    }

    @Override
    public void start() {
        reload();
    }

    @Override
    public void reload() {
        damageTrackers = Caffeine.newBuilder().weakKeys()
                .expireAfterAccess(Duration.ofMinutes(configCenter.config().violence().damageExpireMinutes()))
                .build();
    }

    @Override
    public void stop() { }

    private class DamageTracker {

        private Player lastDamager;
        private final Cache<Player, Boolean> assistants = Caffeine.newBuilder().weakKeys()
                .expireAfterAccess(Duration.ofMinutes(configCenter.config().violence().damageExpireMinutes()))
                .build();

    }
}
