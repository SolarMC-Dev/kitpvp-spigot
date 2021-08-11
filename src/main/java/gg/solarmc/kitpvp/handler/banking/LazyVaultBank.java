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
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.math.BigDecimal;

@Singleton
public final class LazyVaultBank implements Bank {

    private final Server server;
    private final FactoryOfTheFuture futuresFactory;

    private Bank delegate;

    @Inject
    public LazyVaultBank(Server server, FactoryOfTheFuture futuresFactory) {
        this.server = server;
        this.futuresFactory = futuresFactory;
    }

    private Bank delegate() {
        Bank delegate = this.delegate;
        if (delegate == null) {
            delegate = findDelegate();
            this.delegate = delegate;
        }
        return delegate;
    }

    private Bank findDelegate() {
        return futuresFactory.supplySync(() -> {
            Economy vaultEco = server.getServicesManager().load(Economy.class);
            if (vaultEco == null) {
                throw new IllegalStateException(
                        "No Vault Economy provider registered. An economy plugin must be installed");
            }
            return new VaultBank(vaultEco, futuresFactory);
        }).join();
    }

    @Override
    public DepositResult depositBalance(Transaction tx, Player user, BigDecimal amount) {
        return delegate().depositBalance(tx, user, amount);
    }

    @Override
    public WithdrawResult withdrawBalance(Transaction tx, Player user, BigDecimal amount) {
        return delegate().withdrawBalance(tx, user, amount);
    }
}
