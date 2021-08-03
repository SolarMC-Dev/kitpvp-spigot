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

package gg.solarmc.kitpvp.handler.vault;

import gg.solarmc.kitpvp.handler.BankAccess;
import gg.solarmc.loader.Transaction;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.arim.omnibus.util.concurrent.impl.IndifferentFactoryOfTheFuture;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VaultBankAccessTest {

    private final Economy economy;
    private BankAccess bankAccess;

    public VaultBankAccessTest(@Mock Economy economy) {
        this.economy = economy;
    }

    @BeforeEach
    public void setBankAccess() {
        bankAccess = new VaultBankAccess(economy, new IndifferentFactoryOfTheFuture());
    }

    private void assertEqualBigDecimals(BigDecimal expected, BigDecimal actual) {
        // BigDecimal.equals uses the scale whereas compareTo does what we want
        //noinspection SimplifiableAssertion
        assertTrue(expected.compareTo(actual) == 0, "Expected " + expected + " but got " + actual);
    }

    @Test
    public void depositBalance(@Mock Transaction tx, @Mock Player player) {
        when(economy.depositPlayer(eq(player), anyDouble()))
                .thenReturn(new EconomyResponse(1D, 10D, EconomyResponse.ResponseType.SUCCESS, ""));
        BankAccess.DepositResult depositResult = bankAccess.depositBalance(tx, player, BigDecimal.ONE);
        assertEqualBigDecimals(BigDecimal.TEN, depositResult.newBalance());
    }

    @Test
    public void depositBalanceNonPositive(@Mock Transaction tx, @Mock Player player) {
        assertThrows(IllegalArgumentException.class, () -> bankAccess.depositBalance(tx, player, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> bankAccess.depositBalance(tx, player, BigDecimal.valueOf(-1L)));
    }

    @Test
    public void withdrawBalance(@Mock Transaction tx, @Mock Player player) {
        when(economy.withdrawPlayer(eq(player), anyDouble()))
                .thenReturn(new EconomyResponse(1D, 10D, EconomyResponse.ResponseType.SUCCESS, ""));
        BankAccess.WithdrawResult depositResult = bankAccess.withdrawBalance(tx, player, BigDecimal.ONE);
        assertTrue(depositResult.isSuccessful());
        assertEqualBigDecimals(BigDecimal.TEN, depositResult.newBalance());
    }

    @Test
    public void withdrawBalanceInsufficientFunds(@Mock Transaction tx, @Mock Player player) {
        when(economy.withdrawPlayer(eq(player), anyDouble()))
                .thenReturn(new EconomyResponse(1D, 10D, EconomyResponse.ResponseType.FAILURE, "Failed"));
        BankAccess.WithdrawResult depositResult = bankAccess.withdrawBalance(tx, player, BigDecimal.ONE);
        assertFalse(depositResult.isSuccessful());
        assertEqualBigDecimals(BigDecimal.TEN, depositResult.newBalance());
    }

    @Test
    public void withdrawBalanceNonPositive(@Mock Transaction tx, @Mock Player player) {
        assertThrows(IllegalArgumentException.class, () -> bankAccess.withdrawBalance(tx, player, BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> bankAccess.withdrawBalance(tx, player, BigDecimal.valueOf(-1L)));
    }
}
