/*
 * kitpvp
 * Copyright © 2021 SolarMC Developers
 *
 * kitpvp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * kitpvp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with kitpvp. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Affero General Public License.
 */

package gg.solarmc.kitpvp.commands;

import gg.solarmc.command.CommandIterator;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.kitpvp.config.KitConfig;
import gg.solarmc.kitpvp.handler.KitTransferral;
import gg.solarmc.kitpvp.misc.FuturePoster;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.kitpvp.Kit;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class SelectKitCommand extends BaseCommand {

    private final DataCenter dataCenter;
    private final FuturePoster futurePoster;
    private final KitTransferral kitTransferral;

    @Inject
    public SelectKitCommand(ConfigCenter configCenter, DataCenter dataCenter,
                            FuturePoster futurePoster, KitTransferral kitTransferral) {
        super("selectkit", configCenter);
        this.dataCenter = dataCenter;
        this.futurePoster = futurePoster;
        this.kitTransferral = kitTransferral;
    }

    @Override
    public void permission(StringBuilder builder) {
        builder.append("selectkit");
    }

    private KitConfig kitConfig() {
        return config().kitConfig();
    }

    @Override
    public void execute(CommandSender sender, CommandIterator command) {
        if (!command.hasNext()) {
            sender.sendMessage(kitConfig().commands().usageSelectKit());
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("You are not a player"));
            return;
        }
        record KitAndOwnership(Kit kit, boolean owned) {}

        String kitName = command.next();
        futurePoster.postFuture(dataCenter.transact((tx) -> {
            Optional<Kit> optKit = dataCenter.getDataManager(KitPvpKey.INSTANCE).getKitByName(tx, kitName);
            if (optKit.isEmpty()) {
                return new KitAndOwnership(null, false);
            }
            Kit kit = optKit.get();
            boolean owned = player.getSolarPlayer().getData(KitPvpKey.INSTANCE).ownsKit(tx, kit);
            return new KitAndOwnership(kit, owned);
        }).thenAcceptSync((kitAndOwnership) -> {
            Kit selectedKit = kitAndOwnership.kit();
            if (selectedKit == null) {
                sender.sendMessage(kitConfig().commands().selectKitNotFound()
                        .replaceText("%ARGUMENT%", kitName));
                return;
            }
            if (!kitAndOwnership.owned()
                    && !sender.hasPermission(
                            "solar.kitpvp.kit." + kitName + ".forceownership")) {
                sender.sendMessage(kitConfig().noOwnership().replaceText("%KIT%", kitName));
                return;
            }
            kitTransferral.addKitToInventory(selectedKit, player);
        }));
    }
}
