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

package gg.solarmc.kitpvp.misc;

import java.util.function.Supplier;
import java.util.stream.IntStream;

public final class LazyCharSequence implements CharSequence {

    private final Supplier<CharSequence> delegateSupplier;
    private CharSequence delegate;

    public LazyCharSequence(Supplier<CharSequence> delegateSupplier) {
        this.delegateSupplier = delegateSupplier;
    }

    private CharSequence delegate() {
        CharSequence delegate = this.delegate;
        if (delegate == null) {
            delegate = delegateSupplier.get();
            this.delegate = delegate;
        }
        return delegate;
    }

    @Override
    public int length() {
        return delegate().length();
    }

    @Override
    public char charAt(int index) {
        return delegate().charAt(index);
    }

    @Override
    public boolean isEmpty() {
        return delegate().isEmpty();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return delegate().subSequence(start, end);
    }

    @Override
    public String toString() {
        return delegate().toString();
    }

    @Override
    public IntStream chars() {
        return delegate().chars();
    }

    @Override
    public IntStream codePoints() {
        return delegate().codePoints();
    }

}
