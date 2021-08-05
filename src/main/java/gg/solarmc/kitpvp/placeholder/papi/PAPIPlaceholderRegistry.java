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

package gg.solarmc.kitpvp.placeholder.papi;

import gg.solarmc.kitpvp.placeholder.Placeholder;
import gg.solarmc.kitpvp.placeholder.PlaceholderRegistry;
import jakarta.inject.Inject;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class PAPIPlaceholderRegistry implements PlaceholderRegistry {

    private final Server server;

    @Inject
    public PAPIPlaceholderRegistry(Server server) {
        this.server = server;
    }

    @Override
    public Set<RegisteredPlaceholder> registerAll(Map<String, Placeholder> placeholders) {
        PlaceholderExpansion expansion = new PlaceholderExpansion() {

            @Override
            public String getIdentifier() {
                return "placeholderengine";
            }

            @Override
            public String getAuthor() {
                return "SolarMC Developers";
            }

            @Override
            public String getVersion() {
                return "1.0.0-SNAPSHOT";
            }

            @Override
            public String onRequest(OfflinePlayer offlinePlayer, String params) {
                if (!(offlinePlayer instanceof Player player)) {
                    return null;
                }
                Placeholder placeholder = placeholders.get(params);
                if (placeholder == null) {
                    return null;
                }
                return Objects.requireNonNull(placeholder.obtainValue(player), "placeholder value");
            }
        };
        if (expansion.getPlaceholderAPI() == null) {
            // PAPI is full of stupid global state
            return Set.of();
        }
        expansion.register();
        record PAPIRegisteredPlaceholder(PlaceholderExpansion expansion) implements RegisteredPlaceholder {

            @Override
            public void unregister() {
                if (expansion.getPlaceholderAPI() == null) {
                    // PAPI is full of stupid global state
                    return;
                }
                expansion.unregister();
            }
        }
        return Set.of(new PAPIRegisteredPlaceholder(expansion));
    }
}
