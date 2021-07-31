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

import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.math.BigDecimal;

public final class BigDecimalSerializer implements ValueSerialiser<BigDecimal> {

    @Override
    public Class<BigDecimal> getTargetClass() {
        return BigDecimal.class;
    }

    @Override
    public BigDecimal deserialise(FlexibleType flexibleType) throws BadValueException {
        return BigDecimal.valueOf(flexibleType.getLong());
    }

    @Override
    public Object serialise(BigDecimal value, Decomposer decomposer) {
        return value.longValueExact();
    }
}
