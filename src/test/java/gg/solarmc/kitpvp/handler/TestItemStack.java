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

package gg.solarmc.kitpvp.handler;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class TestItemStack extends ItemStack {

    public TestItemStack(Material material) {
        this(material, 0);
    }

    public TestItemStack(Material material, int count) {
        super(material, count);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TestItemStack other = (TestItemStack) obj;
        return getType() == other.getType() && getAmount() == other.getAmount();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getAmount());
    }

    @Override
    public String toString() {
        return "TestItemStack{" +
                "type=" + getType() +
                ", amount=" + getAmount() +
                '}';
    }

    @Override
    public ItemStack clone() {
        return new TestItemStack(getType(), getAmount());
    }

}
