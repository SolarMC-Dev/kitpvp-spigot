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

import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.loader.kitpvp.ItemInSlot;
import gg.solarmc.paper.itemserializer.BukkitKitItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KitTransferralTest {

    private final ConfigCenter configCenter;
    private KitTransferral kitTransferral;

    public KitTransferralTest(@Mock ConfigCenter configCenter) {
        this.configCenter = configCenter;
    }

    @BeforeEach
    public void setKitTransferral() {
        kitTransferral = new KitTransferral(configCenter);
    }

    @Test
    public void obtainItemsFromInventory(@Mock Player source, @Mock PlayerInventory inventory) {
        when(source.getInventory()).thenReturn(inventory);
        ItemStack item = new TestItemStack(Material.STONE);
        when(inventory.getItem(1)).thenReturn(item);
        when(inventory.getItem(not(eq(1)))).thenReturn(null);
        assertEquals(
                Set.of(new ItemInSlot(1, BukkitKitItem.create(item))),
                kitTransferral.obtainItemsFromInventory(source));
    }
}
