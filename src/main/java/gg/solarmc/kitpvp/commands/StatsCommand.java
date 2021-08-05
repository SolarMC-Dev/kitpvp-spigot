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
import gg.solarmc.kitpvp.handler.LevelCalculator;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import gg.solarmc.loader.kitpvp.OnlineKitPvp;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StatsCommand extends BaseCommand {

    private final LevelCalculator levelCalculator;
    private final Server server;

    @Inject
    public StatsCommand(ConfigCenter configCenter, LevelCalculator levelCalculator, Server server) {
        super("stats", configCenter);
        this.levelCalculator = levelCalculator;
        this.server = server;
    }

    @Override
    public void permission(StringBuilder builder) {
        builder.append("stats");
    }

    @Override
    public void execute(CommandSender sender, CommandIterator command) {
        Player target;
        if (command.hasNext()) {
            String targetArgument = command.next();
            target = server.getPlayer(targetArgument);
            if (target == null) {
                sender.sendMessage(config().messages().playerNotFound().replaceText("%ARGUMENT%", targetArgument));
                return;
            }
        } else {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(Component.text("You are not a player"));
                return;
            }
            target = player;
        }
        OnlineKitPvp data = target.getSolarPlayer().getData(KitPvpKey.INSTANCE);
        sender.sendMessage(config().messages().statsSuccess()
                .replaceText("%TARGET%", target.getName())
                .replaceText("%KILLS%", Integer.toString(data.currentKills()))
                .replaceText("%DEATHS%", Integer.toString(data.currentDeaths()))
                .replaceText("%ASSISTS%", Integer.toString(data.currentAssists()))
                .replaceText("%KDR%", String.format("0.2%f", ((double) data.currentKills()) / data.currentDeaths()))
                .replaceText("%KILLSTREAK%", Integer.toString(data.currentCurrentKillstreaks()))
                .replaceText("%HIGHEST_KILLSTREAK%", Integer.toString(data.currentHighestKillstreaks()))
                .replaceText("%EXPERIENCE%", Integer.toString(data.currentExperience()))
                .replaceText("%LEVEL%", Integer.toString(levelCalculator.calculateLevel(data.currentExperience())))
                .asComponent()
                .replaceText((builder) -> {
                    builder.matchLiteral("%FORMATTED_LEVEL%").replacement(levelCalculator.formatLevel(levelCalculator.calculateLevel(data.currentExperience())));
                }));
    }
}
