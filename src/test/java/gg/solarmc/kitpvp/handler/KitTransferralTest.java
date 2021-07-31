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

import gg.solarmc.kitpvp.config.Config;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.kitpvp.config.KitConfig;
import gg.solarmc.loader.kitpvp.ItemInSlot;
import gg.solarmc.loader.kitpvp.Kit;
import gg.solarmc.paper.itemserializer.BukkitKitItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.arim.api.jsonchat.adventure.util.ComponentText;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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

    private void setKitConfig(Consumer<KitConfig> configureMock) {
        Config config = mock(Config.class);
        KitConfig kitConfig = mock(KitConfig.class);
        when(configCenter.config()).thenReturn(config);
        when(config.kitConfig()).thenReturn(kitConfig);
        configureMock.accept(kitConfig);
    }

    @Test
    public void addKitToInventoryAddOrDrop(@Mock Player player,
                                           @Mock PlayerInventory inventory, @Mock World world) {
        setKitConfig((kitConfig) -> {
            when(kitConfig.kitAddMode()).thenReturn(KitConfig.KitAddMode.ADD_OR_DROP);
            when(kitConfig.choseKit()).thenReturn(ComponentText.create(Component.text("Chose kit")));
        });
        Location location = new Location(world, 1D, 1D, 1D);
        Kit kit = mock(Kit.class);
        ItemStack item1 = new TestItemStack(Material.DIAMOND);
        ItemStack item2 = new TestItemStack(Material.BEDROCK);

        when(player.getLocation()).thenReturn(location);
        when(player.getInventory()).thenReturn(inventory);
        when(kit.getName()).thenReturn("KitName");
        when(kit.getContents()).thenReturn(Set.of(
                new ItemInSlot(1, BukkitKitItem.create(item1)),
                new ItemInSlot(2, BukkitKitItem.create(item2))));
        when(inventory.addItem(item1, item2)).thenReturn(new HashMap<>(Map.of(1, item2)));

        kitTransferral.addKitToInventory(kit, player);

        verify(inventory).addItem(item1, item2);
        verify(world).dropItem(location, item2);
        verify(player).sendMessage(ComponentText.create(Component.text("Chose kit")));
    }

    @Test
    public void addKitToInventoryCopyAndSet(@Mock Player player,
                                            @Mock PlayerInventory inventory) {
        setKitConfig((kitConfig) -> {
            when(kitConfig.kitAddMode()).thenReturn(KitConfig.KitAddMode.COPY_AND_SET);
            when(kitConfig.choseKit()).thenReturn(ComponentText.create(Component.text("Chose kit")));
        });
        when(player.getInventory()).thenReturn(inventory);

        Kit kit = mock(Kit.class);
        ItemStack item1 = new TestItemStack(Material.DIAMOND);
        ItemStack item2 = new TestItemStack(Material.BEDROCK);
        when(player.getInventory()).thenReturn(inventory);
        when(kit.getName()).thenReturn("KitName");
        when(kit.getContents()).thenReturn(Set.of(
                new ItemInSlot(1, BukkitKitItem.create(item1)),
                new ItemInSlot(2, BukkitKitItem.create(item2))));

        kitTransferral.addKitToInventory(kit, player);

        verify(inventory).setItem(1, item1);
        verify(inventory).setItem(2, item2);
        verify(player).sendMessage(ComponentText.create(Component.text("Chose kit")));
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
