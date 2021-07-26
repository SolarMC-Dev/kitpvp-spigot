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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;

record PrefilledRangedLookupTable<V>(int firstBound, V[] values, int lastBound) implements RangedLookupTable<V> {

    static <V> RangedLookupTable<V> fromNonEmptyValueRanges(Map<Integer, V> valueRanges, IntFunction<V[]> arrayGenerator) {
        if (valueRanges.isEmpty()) {
            throw new IllegalArgumentException("Ranges are empty");
        }
        List<Integer> keyBounds = new ArrayList<>(valueRanges.keySet());
        keyBounds.sort(null);

        // Determine the first and last bounds
        Integer firstBound = null;
        int lastBound;
        {
            int thisBound;
            for (ListIterator<Integer> boundsIter = keyBounds.listIterator(); ; ) {
                thisBound = boundsIter.next();
                if (firstBound == null) {
                    firstBound = thisBound;
                }
                if (!boundsIter.hasNext()) {
                    lastBound = thisBound;
                    break;
                }
            }
        }
        // Fill the values array
        V[] prefilledValues = arrayGenerator.apply(lastBound - firstBound + 1);
        for (int n = 0; n < keyBounds.size(); n++) {
            int thisBound = keyBounds.get(n);
            V value = valueRanges.get(thisBound);
            if (thisBound == lastBound) {
                prefilledValues[lastBound - firstBound] = value;
                break;
            }
            int nextBound = keyBounds.get(n + 1);
            Arrays.fill(prefilledValues, thisBound - firstBound, nextBound - firstBound, value);
        }
        return new PrefilledRangedLookupTable<>(firstBound, prefilledValues, lastBound);
    }

    @Override
    public Optional<V> findValue(int key) {
        if (key > lastBound) {
            return Optional.of(values[lastBound - firstBound]);
        }
        if (key < firstBound) {
            return Optional.empty();
        }
        return Optional.of(values[key - firstBound]);
    }
}
