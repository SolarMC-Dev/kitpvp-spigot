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

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PrefilledRangedLookupTableTest {

    @Test
    public void exampleFromClassJavadoc() {
        RangedLookupTable<String> lookupTable = PrefilledRangedLookupTable.fromNonEmptyValueRanges(
                Map.of(0, "str1", 6, "str2"), String[]::new);
        assertEquals(Optional.empty(), lookupTable.findValue(-3));
        assertEquals(Optional.empty(), lookupTable.findValue(-1));
        assertEquals(Optional.of("str1"), lookupTable.findValue(0));
        assertEquals(Optional.of("str1"), lookupTable.findValue(2));
        assertEquals(Optional.of("str1"), lookupTable.findValue(5));
        assertEquals(Optional.of("str2"), lookupTable.findValue(6));
        assertEquals(Optional.of("str2"), lookupTable.findValue(7));
        assertEquals(Optional.of("str2"), lookupTable.findValue(Integer.MAX_VALUE));
    }
}
