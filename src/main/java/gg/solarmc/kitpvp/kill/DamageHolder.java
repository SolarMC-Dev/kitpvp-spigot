package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class DamageHolder implements DamageClosable{

    private final KitpvpPlugin plugin;
    private final Map<UUID, BukkitTask> damagers;

    public DamageHolder(KitpvpPlugin plugin) {
        this.plugin = plugin;
        this.damagers = new HashMap<>();
    }

    /**
     * Adds a player to the damagers and then removes them 10 seconds later. This is probably shitcode in someone's book but tbh at this point i don't care.
     * @param damager the one who damages
     */
    public void damage(UUID damager) {

        //if player is not in the map, add the player to the map with a new timer
        //if the player is in the map, cancel the task and add a new timer

        damagers.compute(damager,(id,task) -> {

            if (task != null) {
                task.cancel();
            }

            return damagers.put(damager, plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                damagers.remove(damager);
            }, 20 * 10L));
        });


    }

    /**
     * Returns all players who damaged this damageholder in the last 10 seconds
     * @return an immutable set containing all the damagers
     */
    public Set<UUID> getDamagers() {
        return Set.copyOf(damagers.keySet());
    }

    /**
     * Returns all damagers besides the player who killed
     * @param killer the one player who should not be included
     * @return an immutable set of players without the killer if present
     */
    public Set<UUID> getAssists(UUID killer) {
        //streem api bad says big man (replace this later if necessary, i doubt it)

        Set<UUID> pepee = new HashSet<>();

        for (UUID player : damagers.keySet()) {
            if (!player.equals(killer)) {
                pepee.add(killer);
            }
        }

        return pepee;
    }

    @Override
    public void removeHolder(UUID uuid) {
        damagers.remove(uuid); //don't cancel the task simply remove the link and let java gc clean it up whenever it finishes
    }
}
