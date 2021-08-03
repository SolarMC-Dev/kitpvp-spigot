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
import gg.solarmc.kitpvp.Lifecycle;
import gg.solarmc.kitpvp.config.Config;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.kitpvp.handler.KitTransferral;
import gg.solarmc.kitpvp.misc.FuturePoster;
import gg.solarmc.loader.DataCenter;
import gg.solarmc.loader.kitpvp.ItemInSlot;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Set;

public class AdminCommand extends BaseCommand {

    private final DataCenter dataCenter;
    private final FuturePoster futurePoster;
    // Use provider to break circular dependencies
    private final Provider<Lifecycle> lifecycleProvider;
    private final KitTransferral kitTransferral;

    @Inject
    public AdminCommand(ConfigCenter configCenter, DataCenter dataCenter, FuturePoster futurePoster,
                        Provider<Lifecycle> lifecycleProvider, KitTransferral kitTransferral) {
        super("kitpvp-admin", configCenter);
        this.dataCenter = dataCenter;
        this.futurePoster = futurePoster;
        this.lifecycleProvider = lifecycleProvider;
        this.kitTransferral = kitTransferral;
    }

    @Override
    public void permission(StringBuilder builder) {
        builder.append("admin");
    }

    private Config.Messages messages() {
        return config().messages();
    }

    @Override
    public void execute(CommandSender sender, CommandIterator command) {
        if (!command.hasNext()) {
            sender.sendMessage(messages().adminUsage());
            return;
        }
        String firstArg = command.next().toLowerCase(Locale.ROOT);
        switch (firstArg) {
            case "reload" -> {
                lifecycleProvider.get().reload();
                sender.sendMessage(messages().reloaded());
            }
            case "createkit", "deletekit" -> {
                if (!command.hasNext()) {
                    sender.sendMessage(messages().requireKitName());
                    return;
                }
                String kitName = command.next();
                if (firstArg.equals("deletekit")) {
                    futurePoster.postFuture(dataCenter.transact((tx) -> {
                        return dataCenter.getDataManager(KitPvpKey.INSTANCE).deleteKitByName(tx, kitName);
                    }).thenAccept((deleted) -> {
                        if (!deleted) {
                            sender.sendMessage(messages().kitNotFound().replaceText("%ARGUMENT%", kitName));
                            return;
                        }
                        sender.sendMessage(messages().deleteKitSuccess().replaceText("%KIT%", kitName));
                    }));
                    return;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Component.text("You are not a player"));
                    return;
                }
                Set<ItemInSlot> contents = kitTransferral.obtainItemsFromInventory(player);
                futurePoster.postFuture(dataCenter.transact((tx) -> {
                    return dataCenter.getDataManager(KitPvpKey.INSTANCE)
                            .createKit(tx, kitName, contents);
                }).thenAccept((kit) -> {
                    if (kit.isEmpty()) {
                        player.sendMessage(messages().createKitAlreadyExists().replaceText("%KIT%", kitName));
                        return;
                    }
                    player.sendMessage(messages().createKitSuccess().replaceText("%KIT%", kitName));
                }));
            }
            default -> sender.sendMessage(config().messages().adminUsage());
        }
    }

}
