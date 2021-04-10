package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Map of all players to their respective holders. No need for concurrency.
 */
public class DamageMap implements DamageClosable{

    private Map<UUID, DamageHolder> handlerMap;
    private KitpvpPlugin plugin;

    public DamageMap(KitpvpPlugin plugin) {
        this.plugin = plugin;
        this.handlerMap = new HashMap<>();
    }

    public void trackDamage(UUID damager, UUID damaged) {
        this.getHolder(damaged).damage(damager);
    }

    /**
     * Puts a new damageholder in the internal map to wipe data
     * Called on kill
     *
     * @param wiped the person who is to have their damageholder wiped
     */
    public void wipeHolder(UUID wiped) {
        handlerMap.put(wiped,new DamageHolder(plugin));
    }

    public void removeHolder(UUID wiped) {
        handlerMap.remove(wiped);

        handlerMap.values().forEach(holder -> {
            holder.removeHolder(wiped);
        });
    }

    /**
     * Gets the dataholder of a player or returns a new dataholder
     * @param key the player
     * @return a new dataholder
     */
    public DamageHolder getHolder(UUID key) {
        return handlerMap.computeIfAbsent(key, (ignored) -> new DamageHolder(plugin));
    }





}
