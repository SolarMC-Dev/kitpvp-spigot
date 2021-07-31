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
import gg.solarmc.loader.kitpvp.Kit;
import gg.solarmc.paper.itemserializer.BukkitKitItem;
import jakarta.inject.Inject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;

public class KitTransferral {

    private final ConfigCenter config;

    @Inject
    public KitTransferral(ConfigCenter config) {
        this.config = config;
    }

    public void addKitToInventory(Kit kit, Player target) {
        PlayerInventory inventory = target.getInventory();
        for (ItemInSlot itemInSlot : kit.getContents()) {
            inventory.setItem(itemInSlot.slot(), itemInSlot.item().getItem(ItemStack.class));
        }
        target.sendMessage(config.config().kitConfig().choseKit().replaceText("%KIT%", kit.getName()));
    }

    private static final int INVENTORY_SIZE = 40;

    public Set<ItemInSlot> obtainItemsFromInventory(Player source) {
        PlayerInventory inventory = source.getInventory();;
        Set<ItemInSlot> contents = new HashSet<>(INVENTORY_SIZE);
        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item == null) {
                continue;
            }
            contents.add(new ItemInSlot(slot, BukkitKitItem.create(item)));
        }
        return contents;
    }
}
