package gg.solarmc.kitpvp.kill;

import gg.solarmc.kitpvp.KitpvpPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Map of all players to their respective holders. No need for concurrency.
 */
public class DamageMap implements DamageClosable {

    private Map<Player, DamageHolder> handlerMap;
    private KitpvpPlugin plugin;

    public DamageMap(KitpvpPlugin plugin) {
        this.plugin = plugin;
        this.handlerMap = new HashMap<>();
    }

    public void trackDamage(Player damager, Player damaged) {
        handlerMap.computeIfAbsent(damaged,(ignored) -> new DamageHolder(plugin)).damage(damager);
    }

    /**
     * Puts a new damageholder in the internal map to wipe data
     * Called on kill or death
     *
     * @param wiped the person who is to have their damageholder wiped
     */
    public void wipeHolder(Player wiped) {
        handlerMap.put(wiped,new DamageHolder(plugin));
    }

    /**
     * Gets the dataholder of a player or returns a new dataholder
     * @param key the player
     * @return a new dataholder
     */
    public DamageHolder getHolder(Player key) {
        return handlerMap.computeIfAbsent(key, (ignored) -> new DamageHolder(plugin));
    }


    @Override
    public void close(Player player) {
        this.handlerMap.remove(player).close(player);
    }
}
