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

package gg.solarmc.kitpvp;

import gg.solarmc.kitpvp.commands.CommandRegistration;
import gg.solarmc.kitpvp.config.MainConfigCenter;
import gg.solarmc.kitpvp.listeners.KillListener;
import gg.solarmc.kitpvp.listeners.ListenerRegistration;
import gg.solarmc.kitpvp.placeholder.PlaceholderRegistration;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public final class Lifecycle {

    private final List<HasLifecycle> objectsWithLifecycles;

    public Lifecycle(List<HasLifecycle> objectsWithLifecycles) {
        this.objectsWithLifecycles = List.copyOf(objectsWithLifecycles);
    }

    @Inject
    public Lifecycle(MainConfigCenter configCenter, KillListener killListener,
                     ListenerRegistration listenerRegistration, CommandRegistration commandRegistration,
                     PlaceholderRegistration placeholderRegistration) {
        this(List.of(configCenter, killListener, listenerRegistration, commandRegistration, placeholderRegistration));
    }

    void start() {
        objectsWithLifecycles.forEach(HasLifecycle::start);
    }

    public void reload() {
        objectsWithLifecycles.forEach(HasLifecycle::reload);
    }

    void stop() {
        objectsWithLifecycles.forEach(HasLifecycle::stop);
    }
}
