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

package gg.solarmc.kitpvp.placeholder;

import gg.solarmc.kitpvp.HasLifecycle;
import gg.solarmc.kitpvp.handler.LevelCalculator;
import gg.solarmc.loader.kitpvp.KitPvpKey;
import gg.solarmc.loader.kitpvp.OnlineKitPvp;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class PlaceholderRegistration implements HasLifecycle {

    private final PlaceholderRegistry placeholderRegistry;
    private final LevelCalculator levelCalculator;

    private Set<PlaceholderRegistry.RegisteredPlaceholder> registered;

    @Inject
    public PlaceholderRegistration(PlaceholderRegistry placeholderRegistry, LevelCalculator levelCalculator) {
        this.placeholderRegistry = placeholderRegistry;
        this.levelCalculator = levelCalculator;
    }

    @Override
    public void start() {
        var state = new RegistrationState();
        state.registerAs("kills", OnlineKitPvp::currentKills);
        state.registerAs("deaths", OnlineKitPvp::currentDeaths);
        state.registerAs("assists", OnlineKitPvp::currentAssists);
        state.registerAs("kdr",
                (data) -> String.format("0.2%f", ((double) data.currentKills()) / data.currentDeaths()));
        state.registerAs("current_killstreak", OnlineKitPvp::currentCurrentKillstreaks);
        state.registerAs("highest_killstreak", OnlineKitPvp::currentHighestKillstreaks);
        state.registerAs("level", (data) -> levelCalculator.calculateLevel(data.currentExperience()));
        registered = state.finalizeRegistration();
    }

    private class RegistrationState {

        private final Map<String, Placeholder> placeholders = new HashMap<>();

        void register(String placeholderSuffix, Placeholder placeholder) {
            placeholders.put("solar_kitpvp_" + placeholderSuffix, placeholder);
        }

        void registerAs(String placeholderSuffix, Function<OnlineKitPvp, Object> placeholder) {
            register(placeholderSuffix, (player) -> {
                OnlineKitPvp data = player.getSolarPlayer().getData(KitPvpKey.INSTANCE);
                return String.valueOf(placeholder.apply(data));
            });
        }

        Set<PlaceholderRegistry.RegisteredPlaceholder> finalizeRegistration() {
            return placeholderRegistry.registerAll(placeholders);
        }

    }

    @Override
    public void reload() { }

    @Override
    public void stop() {
        registered.forEach(PlaceholderRegistry.RegisteredPlaceholder::unregister);
    }
}
