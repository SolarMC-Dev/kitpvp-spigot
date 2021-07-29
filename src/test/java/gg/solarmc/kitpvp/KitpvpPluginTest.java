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

import gg.solarmc.kitpvp.listeners.KillListener;
import gg.solarmc.loader.DataCenter;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.LaunchablePlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KitpvpPluginTest {

    private final Plugin plugin;
    private final Server server;
    private LaunchablePlugin kitpvp;

    @TempDir
    public Path folder;

    public KitpvpPluginTest(@Mock Plugin plugin, @Mock Server server) {
        this.plugin = plugin;
        this.server = server;
    }

    @BeforeEach
    public void setKitpvp(@Mock DataCenter dataCenter) {
        when(plugin.getServer()).thenReturn(server);
        when(server.getDataCenter()).thenReturn(dataCenter);
        kitpvp = new KitpvpPlugin();
    }

    @Test
    public void onLaunch() {
        assertDoesNotThrow(() -> kitpvp.onLaunch(plugin, folder));
    }

    @Test
    public void onEnable(@Mock PluginManager pluginManager, @Mock CommandMap commandMap) {
        when(server.getPluginManager()).thenReturn(pluginManager);
        when(server.getCommandMap()).thenReturn(commandMap);
        kitpvp.onLaunch(plugin, folder);
        kitpvp.onEnable();
        verify(pluginManager).registerEvents(isA(KillListener.class), eq(plugin));
        assertTrue(Files.exists(folder.resolve("config.yml")));
    }
}
