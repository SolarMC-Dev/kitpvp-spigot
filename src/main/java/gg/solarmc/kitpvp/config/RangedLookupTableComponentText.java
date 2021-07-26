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

import space.arim.api.jsonchat.adventure.util.ComponentText;

import java.util.Map;
import java.util.Optional;

public record RangedLookupTableComponentText(RangedLookupTable<ComponentText> delegate,
                                             Map<Integer, ComponentText> originalInput)
        implements RangedLookupTable.WithOriginalValueRanges<ComponentText> {

    @Override
    public Optional<ComponentText> findValue(int key) {
        return delegate.findValue(key);
    }

    public static class Serializer
            extends RangedLookupTableSerializer<ComponentText, RangedLookupTableComponentText> {

        @Override
        public Class<RangedLookupTableComponentText> getTargetClass() {
            return RangedLookupTableComponentText.class;
        }

        @Override
        Class<ComponentText> getValueType() {
            return ComponentText.class;
        }

        @Override
        RangedLookupTableComponentText fromDelegate(RangedLookupTable<ComponentText> delegate,
                                                    Map<Integer, ComponentText> originalInput) {
            return new RangedLookupTableComponentText(delegate, originalInput);
        }
    }
}
