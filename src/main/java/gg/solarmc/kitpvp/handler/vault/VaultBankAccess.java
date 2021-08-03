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
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.math.BigDecimal;

public final class VaultBankAccess implements BankAccess {

    private final Economy vaultEco;
    private final FactoryOfTheFuture futuresFactory;

    public VaultBankAccess(Economy vaultEco, FactoryOfTheFuture futuresFactory) {
        this.vaultEco = vaultEco;
        this.futuresFactory = futuresFactory;
    }

    @Override
    public DepositResult depositBalance(Transaction tx, Player user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        return futuresFactory.supplySync(() -> {
            EconomyResponse response = vaultEco.depositPlayer(user, amount.doubleValue());
            if (!response.transactionSuccess()) {
                throw new IllegalStateException("Unable to perform depositBalance");
            }
            return new DepositResult(BigDecimal.valueOf(response.balance));
        }).join();
    }

    @Override
    public WithdrawResult withdrawBalance(Transaction tx, Player user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        return futuresFactory.supplySync(() -> {
            EconomyResponse response = vaultEco.withdrawPlayer(user, amount.doubleValue());
            return new WithdrawResult(response.transactionSuccess(), BigDecimal.valueOf(response.balance));
        }).join();
    }
}
