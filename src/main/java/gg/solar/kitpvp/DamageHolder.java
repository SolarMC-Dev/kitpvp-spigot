package gg.solar.kitpvp;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DamageHolder {

    private final KitpvpPlugin plugin;
    private final Map<Player, BukkitTask> damagers;

    public DamageHolder(KitpvpPlugin plugin) {
        this.plugin = plugin;
        this.damagers = new HashMap<>();
    }

    /**
     * Adds a player to the damagers and then removes them 10 seconds later. This is probably shitcode in someone's book but tbh at this point i don't care.
     * @param damager the one who damages
     */
    public void damage(Player damager) {

        //if player is not in the map, add the player to the map with a new timer
        //if the player is in the map, cancel the task and add a new timer

        if (damagers.containsKey(damager)) {
            damagers.get(damager).cancel();
        }

        damagers.put(damager, plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            damagers.remove(damager);
        }, 20 * 10L));

    }

    /**
     * Returns all players who damaged this damageholder in the last 10 seconds
     * @return all players
     */
    public Set<Player> getDamagers() {
        return this.damagers.keySet();
    }

    /**
     * Returns all damagers besides the player who killed
     * @param killer the one player who should not be included
     * @return an immutable set of players without the killer if present
     */
    public Set<Player> getAssists(Player killer) {
        //streem api bad says big man (replace this later if necessary, i doubt it)
        return this.damagers.keySet().stream().filter(player -> !player.equals(killer)).collect(Collectors.toUnmodifiableSet());
    }

}
