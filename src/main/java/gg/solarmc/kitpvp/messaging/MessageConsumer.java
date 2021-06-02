package gg.solarmc.kitpvp.messaging;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.UUID;

@FunctionalInterface
public interface MessageConsumer {

    void consume(Player player, String message);

}
