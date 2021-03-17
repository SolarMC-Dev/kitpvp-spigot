package gg.solarmc.kitpvp.messaging;

import org.bukkit.entity.Player;

public enum Prefix {

    ACTIONBAR("[ACTIONBAR]", Player::sendActionBar),
    CHAT("[CHAT]",Player::sendMessage),
    TITLE("[TITLE]",(s,e) -> {
        s.sendTitle(e,"",0,10,10);
    }),
    SUBTITLE("[SUBTITLE]", (s,e) -> {
        s.sendTitle("",e,0,10,10);
    }),
    BROADCAST("[BROADAST]", (s,e) -> {
        for (Player player : s.getServer().getOnlinePlayers()) {
            player.sendMessage(e);
        }
    }),
    NONE("",Player::sendMessage);

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

    static void message(Player player, String message) {
        Prefix prefix = getPrefix(message);

        String subMessage = message.substring(prefix.getIdentifier().length());

        prefix.getConsumer().consume(player,subMessage);
    }
}
