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

package gg.solarmc.kitpvp.listeners;

import gg.solarmc.kitpvp.HasLifecycle;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.List;

@Singleton
public final class ListenerRegistration implements HasLifecycle {

    private final Plugin plugin;
    private final List<Listener> listeners;

    public ListenerRegistration(Plugin plugin, List<Listener> listeners) {
        this.plugin = plugin;
        this.listeners = List.copyOf(listeners);
    }

    @Inject
    public ListenerRegistration(Plugin plugin, KillListener killListener) {
        this(plugin, List.of(killListener));
    }

    @Override
    public void start() {
        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    @Override
    public void reload() { }

    @Override
    public void stop() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }
}
