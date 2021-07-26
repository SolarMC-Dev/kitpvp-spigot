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

import jakarta.inject.Inject;
import jakarta.inject.Named;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.CommentMode;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Objects;

public final class MainConfigCenter implements ConfigCenter {

    private final Path folder;
    private final ConfigurationOptions options;

    private volatile Config config;

    @Inject
    public MainConfigCenter(@Named("folder") Path folder, ConfigurationOptions options) {
        this.folder = folder;
        this.options = options;
    }

    @Override
    public void start() {
        reload();
    }

    @Override
    public void reload() {
        ConfigurationHelper<Config> configHelper = new ConfigurationHelper<>(folder, "config.yml",
                SnakeYamlConfigurationFactory.create(Config.class, options,
                        new SnakeYamlOptions.Builder().commentMode(CommentMode.fullComments()).build()));
        try {
            config = configHelper.reloadConfigData();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        } catch (InvalidConfigException ex) {
            throw new RuntimeException("Please fix the config and reload/restart", ex);
        }

    }

    @Override
    public void stop() {

    }

    @Override
    public Config config() {
        return Objects.requireNonNull(config, "Internal failure");
    }

}
