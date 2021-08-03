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

import gg.solarmc.kitpvp.InjectableConstructor;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.Test;

public class ListenerRegistrationTest {

    @Test
    public void allDeclared() {
        new InjectableConstructor(ListenerRegistration.class).verifyParametersContainSubclassesOf(Listener.class);
    }
}