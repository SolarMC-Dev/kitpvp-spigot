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

import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;

/**
 * An immutable table which relates a range of integer keys to a value.
 * For example, values 0 through 5 might map to "str1" while values 6
 * and greater map to "str2"
 *
 * @param <V> the value type
 */
public interface RangedLookupTable<V> {

    /**
     * Gets the value associated with the given key
     *
     * @param key the key
     * @return the value, or {@code null} if there is none
     */
    Optional<V> findValue(int key);

    /**
     * Creates from multiple ranges. Following the same example in the class javadoc,
     * a map of 0 to "str1" and 6 to "str2" will create a lookup table where values 0
     * through 5 correspond to "str1" and  values 6 and greater to "str2"
     *
     * @param valueRanges a map of value ranges, where the keys are inclusive in the range,
     *                    and the range extends upward of the key
     * @param arrayGenerator to workaround generics limitations in the implementation
     * @param <V> the value type
     * @return a lookup table
     * @throws IllegalArgumentException if the ranges are empty
     */
    static <V> RangedLookupTable<V> fromValueRanges(Map<Integer, V> valueRanges, IntFunction<V[]> arrayGenerator) {
        return PrefilledRangedLookupTable.fromNonEmptyValueRanges(valueRanges, arrayGenerator);
    }

    interface WithOriginalValueRanges<V> extends RangedLookupTable<V> {

        Map<Integer, V> originalInput();
    }
}

