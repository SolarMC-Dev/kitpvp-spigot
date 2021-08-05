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

import gg.solarmc.kitpvp.config.BigDecimalSerializer;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.kitpvp.config.MainConfigCenter;
import gg.solarmc.kitpvp.config.PossibleSound;
import gg.solarmc.kitpvp.config.RangedLookupTableBigDecimal;
import gg.solarmc.kitpvp.config.RangedLookupTableComponentText;
import gg.solarmc.kitpvp.handler.BankAccess;
import gg.solarmc.kitpvp.handler.vault.LazyVaultBankAccess;
import gg.solarmc.kitpvp.misc.FuturePoster;
import gg.solarmc.kitpvp.misc.LoggingFuturePoster;
import gg.solarmc.kitpvp.placeholder.PlaceholderRegistry;
import gg.solarmc.kitpvp.placeholder.papi.PAPIPlaceholderRegistry;
import gg.solarmc.loader.DataCenter;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import space.arim.api.jsonchat.adventure.ChatMessageComponentSerializer;
import space.arim.api.util.dazzleconf.ChatMessageSerializer;
import space.arim.api.util.dazzleconf.ComponentTextSerializer;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public class MainBindModule {

    public Server server(Plugin plugin) {
        return plugin.getServer();
    }

    public DataCenter dataCenter(Server server) {
        return server.getDataCenter();
    }

    public ConfigCenter configCenter(MainConfigCenter configCenter) {
        return configCenter;
    }

    public FactoryOfTheFuture futuresFactory(Server server) {
        return server.getOmnibus().getRegistry().getProvider(FactoryOfTheFuture.class).orElseThrow();
    }

    public FuturePoster futurePoster(LoggingFuturePoster futurePoster) {
        return futurePoster;
    }

    public ConfigurationOptions configOptions() {
        return new ConfigurationOptions.Builder()
                .addSerialisers(
                        new ChatMessageSerializer(new ChatMessageComponentSerializer()),
                        new ComponentTextSerializer())
                .addSerialiser(new BigDecimalSerializer())
                .addSerialiser(new RangedLookupTableBigDecimal.Serializer())
                .addSerialiser(new RangedLookupTableComponentText.Serializer())
                .addSerialisers(new PossibleSound.SoundSerializer())
                .build();
    }

    public BankAccess bankAccess(LazyVaultBankAccess bankAccess) {
        return bankAccess;
    }

    public PlaceholderRegistry placeholderRegistry(PAPIPlaceholderRegistry placeholderRegistry) {
        return placeholderRegistry;
    }

}
