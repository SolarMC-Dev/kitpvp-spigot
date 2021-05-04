package gg.solarmc.kitpvp.messaging;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.UUID;

public enum Prefix {

    ACTIONBAR("[ACTIONBAR]", (s,e,ser) -> {
        Player player = ser.getPlayer(s);

        if (player != null) {

            //because we have kyori now i need to figure out how to change this
        }
    }),
    CHAT("[CHAT]",(s,e,ser) -> {
        Player player = ser.getPlayer(s);

        if (player != null) {
            player.sendMessage(e);
        }
    }),
    TITLE("[TITLE]",(s,e,ser) -> {
        Player player = ser.getPlayer(s);

        if (player != null) {
            player.sendTitle(e,"",0,10,10);
        }
    }),
    SUBTITLE("[SUBTITLE]", (s,e,ser) -> {
        Player player = ser.getPlayer(s);

        if (player != null) {
            player.sendTitle(e,"",0,10,10);
        }
    }),
    BROADCAST("[BROADCAST]", (s,e,ser) -> {
        for (Player player : ser.getOnlinePlayers()) {
            player.sendMessage(e);
        }
    }),
    NONE("",(s,e,ser) -> {
        Player player = ser.getPlayer(s);

        if (player != null) {
            player.sendMessage(e);
        }
    });

    public String getIdentifier() {
        return identifier;
    }

    public MessageConsumer getConsumer() {
        return consumer;
    }

    private final String identifier;
    private final MessageConsumer consumer;

    Prefix(String identifier, MessageConsumer sendMessage) {
        this.identifier = identifier;
        this.consumer = sendMessage;
    }

    static Prefix getPrefix(String message) {
        for (Prefix prefix : Prefix.values()) {
            if (message.startsWith(prefix.getIdentifier())) return prefix;
        }

        return Prefix.NONE;
    }

    static void message(UUID player, String message, Server server) {
        Prefix prefix = getPrefix(message);

        String subMessage = message.substring(prefix.getIdentifier().length());

        prefix.getConsumer().consume(player,subMessage,server);
    }

}
