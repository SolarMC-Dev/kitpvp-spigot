package gg.solarmc.kitpvp.stat;

import gg.solarmc.kitpvp.KitpvpPlugin;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import gg.solarmc.loader.kitpvp.OnlineKitPvp;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final KitpvpPlugin plugin;

    public PlaceholderAPIHook(KitpvpPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "kitpvp";
    }

    @Override
    public String getAuthor() {
        return "Aurium";
    }

    @Override
    public String getVersion() {
        return "0.1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null) { return ""; }

        OnlineKitPvp pvp = player.getSolarPlayer().toLivePlayer().orElseThrow().getData(KitPvpKey.INSTANCE);

        //look how good this looks :) (the cool switch case)
        return switch (identifier) {
            case "kills" -> String.valueOf(pvp.currentKills());
            case "deaths" -> String.valueOf(pvp.currentDeaths());
            case "assists" -> String.valueOf(pvp.currentAssists());
            case "kda" -> String.valueOf(pvp.currentKills() / pvp.currentDeaths());
            default -> "";
        };

    }
}
