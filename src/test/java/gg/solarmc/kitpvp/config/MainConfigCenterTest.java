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

package gg.solarmc.kitpvp.config;

import gg.solarmc.kitpvp.MainBindModule;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class MainConfigCenterTest {

    @TempDir
    public Path folder;

    private MainConfigCenter configCenter;

    @BeforeEach
    public void setConfigCenter(@Mock Plugin plugin, @Mock Server server) {
        configCenter = new MainConfigCenter(folder, new MainBindModule().configOptions());
    }

    @Test
    public void loadDefaultConfig() throws IOException {
        assertDoesNotThrow(configCenter::reload);
        assertNotNull(configCenter.config());
    }

    @Test
    public void reloadDefaultConfig() {
        configCenter.reload();
        assertNotNull(configCenter.config());
        assertDoesNotThrow(configCenter::reload);
    }

}
