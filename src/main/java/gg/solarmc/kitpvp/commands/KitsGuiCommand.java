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

import gg.solarmc.kitpvp.config.ConfigCenter;
import jakarta.inject.Inject;
import org.bukkit.command.CommandSender;

public class KitsGuiCommand extends BaseCommand {

    @Inject
    public KitsGuiCommand(ConfigCenter configCenter) {
        super("kitgui", configCenter);
    }

    @Override
    public void permission(StringBuilder builder) {
        builder.append("kitgui");
    }

    @Override
    public void execute(CommandSender sender, CommandIterator command) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
