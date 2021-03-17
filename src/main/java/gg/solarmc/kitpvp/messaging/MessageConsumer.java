package gg.solarmc.kitpvp.messaging;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface MessageConsumer {

    void consume(Player player, String message);

}
