package gg.solarmc.kitpvp.messaging;

import org.bukkit.Server;

import java.util.UUID;

@FunctionalInterface
public interface MessageConsumer {

    void consume(UUID player, String message, Server server);

}
