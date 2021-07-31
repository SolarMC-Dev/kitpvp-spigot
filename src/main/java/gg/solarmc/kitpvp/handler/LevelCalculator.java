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

package gg.solarmc.kitpvp.handler;

import gg.solarmc.kitpvp.config.ConfigCenter;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import space.arim.api.jsonchat.adventure.util.ComponentText;

public class LevelCalculator {

    private final ConfigCenter configCenter;

    @Inject
    public LevelCalculator(ConfigCenter configCenter) {
        this.configCenter = configCenter;
    }

    public int calculateLevel(int experience) {
        // 1/2+(2x+1/4)^(1/2)
        return (int) Math.floor(0.5D + Math.sqrt(experience * 2 + 0.25D));
    }

    public ComponentLike formatLevel(int level) {
        return configCenter.config().levels().levelFormatting().findValue(level)
                .orElse(ComponentText.create(Component.empty()))
                .replaceText("%LEVEL%", Integer.toString(level));
    }
}
