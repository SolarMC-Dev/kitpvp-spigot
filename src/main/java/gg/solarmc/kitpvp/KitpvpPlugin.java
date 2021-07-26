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

import org.bukkit.plugin.LaunchablePlugin;
import org.bukkit.plugin.Plugin;
import org.slf4j.LoggerFactory;
import space.arim.injector.Identifier;
import space.arim.injector.InjectorBuilder;
import space.arim.injector.SpecificationSupport;

import java.nio.file.Path;

public final class KitpvpPlugin implements LaunchablePlugin {

    private Lifecycle lifecycle;

    @Override
    public void onLaunch(Plugin plugin, Path path) {
        LoggerFactory.getLogger(getClass())
                .info("Loading KitPvP plugin by Aurium / Aesthetik and A248");
        lifecycle = new InjectorBuilder().specification(SpecificationSupport.JAKARTA)
                .addBindModules(new MainBindModule())
                .bindInstance(Plugin.class, plugin)
                .bindInstance(Identifier.ofTypeAndNamed(Path.class, "folder"), path)
                .build()
                .request(Lifecycle.class);
    }

    @Override
    public void onEnable() {
        lifecycle.start();
    }

    @Override
    public void close() {
        lifecycle.stop();
    }
}
