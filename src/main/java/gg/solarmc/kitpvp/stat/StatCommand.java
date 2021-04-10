package gg.solarmc.kitpvp.stat;

import gg.solarmc.kitpvp.messaging.MessageConfig;
import gg.solarmc.kitpvp.messaging.MessageController;
import gg.solarmc.kitpvp.messaging.parsers.StatParser;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import gg.solarmc.loader.kitpvp.OnlineKitPvp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//simple command because i cannot be arsed to finish Branch Framework right now



//TODO reimplement this on Branch 1.0 launch


/**
 * TODO: simple command that tells the stats of the sender. Once api changes and branch framework are done, move this to a Branch-style system
 * where you can also see the stats of an existing player.
 */
/*
public class StatCommand implements Command<CommandSender> {

    private final MessageConfig messageConfig;
    private final DataCenter center;

    public StatCommand(MessageConfig messageConfig, DataCenter center) {
        this.messageConfig = messageConfig;
        this.center = center;
    }

    @Override
    public String getName() {
        return "stats";
    }

    @Override
    public String getPermission() {
        return "kitpvp.stats";
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("not as console");
            return true;
        }

        Player player = (Player) sender;

        center.transact(transaction -> {
            OnlineKitPvp pvp = player.getSolarPlayer().getData(KitPvpKey.INSTANCE);

            return new StatParser(pvp.currentKills(), pvp.currentDeaths(), pvp.currentAssists(), 0,0,0,0,0);
        }).thenAcceptSync(parser -> {
            MessageController.statType(messageConfig.getStatMessage(),parser).target(player);
        });

        return true;
    }

}
*/
