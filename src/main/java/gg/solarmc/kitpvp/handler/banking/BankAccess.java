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

import gg.solarmc.loader.kitpvp.BountyCurrency;
import jakarta.inject.Inject;

import java.util.Map;

public final class BankAccess {

    private final Map<BountyCurrency, Bank> banks;

    public BankAccess(Map<BountyCurrency, Bank> banks) {
        this.banks = banks;
    }

    @Inject
    public BankAccess(CreditsBank creditsBank, LazyVaultBank vaultBank) {
        this(Map.of(BountyCurrency.CREDITS, creditsBank, BountyCurrency.PLAIN_ECO, vaultBank));
        for (BountyCurrency currency : BountyCurrency.values()) {
            assert banks.containsKey(currency);
        }
    }

    public Bank findBank(BountyCurrency currency) {
        Bank bank = banks.get(currency);
        if (bank == null) {
            throw new IllegalStateException("No bank for " + currency);
        }
        return bank;
    }
}
