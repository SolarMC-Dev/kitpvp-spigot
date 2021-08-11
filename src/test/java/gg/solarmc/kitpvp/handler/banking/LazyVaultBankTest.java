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

package gg.solarmc.kitpvp.handler.banking;

import gg.solarmc.loader.Transaction;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.arim.omnibus.util.concurrent.impl.IndifferentFactoryOfTheFuture;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LazyVaultBankTest {

    private final ServicesManager servicesManager;
    private Bank bank;

    public LazyVaultBankTest(@Mock ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    @BeforeEach
    public void setBankAccess(@Mock Server server) {
        when(server.getServicesManager()).thenReturn(servicesManager);
        bank = new LazyVaultBank(server, new IndifferentFactoryOfTheFuture());
    }

    @Test
    public void noRegisteredProvider(@Mock Transaction tx, @Mock Player user) {
        BigDecimal amount = BigDecimal.TEN;
        assertThrows(RuntimeException.class, () -> bank.depositBalance(tx, user, amount));
        assertThrows(RuntimeException.class, () -> bank.withdrawBalance(tx, user, amount));
    }

    @Test
    public void hasRegisteredProvider(@Mock Economy economy,
                                      @Mock Transaction tx, @Mock Player user) {
        {
            when(servicesManager.load(Economy.class)).thenReturn(economy);
            var response = new EconomyResponse(10D, 100D, EconomyResponse.ResponseType.SUCCESS, "");
            when(economy.depositPlayer(eq(user), anyDouble())).thenReturn(response);
            when(economy.withdrawPlayer(eq(user), anyDouble())).thenReturn(response);
        }
        BigDecimal amount = BigDecimal.TEN;
        assertDoesNotThrow(() -> bank.depositBalance(tx, user, amount));
        assertDoesNotThrow(() -> bank.withdrawBalance(tx, user, amount));
    }
}
