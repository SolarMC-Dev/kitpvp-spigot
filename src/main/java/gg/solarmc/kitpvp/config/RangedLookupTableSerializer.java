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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntFunction;

abstract class RangedLookupTableSerializer<V, T extends RangedLookupTable.WithOriginalValueRanges<V>>
        implements ValueSerialiser<T> {

    abstract Class<V> getValueType();

    abstract T fromDelegate(RangedLookupTable<V> delegate, Map<Integer, V> originalInput);

    @Override
    public T deserialise(FlexibleType flexibleType) throws BadValueException {
        Class<V> valueType = getValueType();
        Map<Integer, V> originalInput = flexibleType.getMap(
                (key, value) -> Map.entry(key.getInteger(), value.getObject(valueType)));
        @SuppressWarnings("unchecked")
        IntFunction<V[]> arrayGenerator = (size) -> (V[]) Array.newInstance(valueType, size);
        return fromDelegate(
                RangedLookupTable.fromValueRanges(originalInput, arrayGenerator),
                originalInput);
    }

    @Override
    public Object serialise(T value, Decomposer decomposer) {
        Class<V> valueType = getValueType();
        Map<Integer, V> originalInput = value.originalInput();
        Map<Integer, Object> decomposed = new HashMap<>(originalInput.size());
        for (Map.Entry<Integer, V> originalEntry : originalInput.entrySet()) {
            decomposed.put(originalEntry.getKey(), decomposer.decompose(valueType, originalEntry.getValue()));
        }
        return decomposed;
    }

}
