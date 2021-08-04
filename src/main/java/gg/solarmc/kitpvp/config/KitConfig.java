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

import net.kyori.adventure.text.Component;
import space.arim.api.jsonchat.adventure.util.ComponentText;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

public interface KitConfig {

    @SubSection
    Commands commands();

    interface Commands {

        @ConfKey("usage-selectkit")
        @ConfDefault.DefaultString("&cUsage: /selectkit <kit>")
        Component usageSelectKit();

        @ConfKey("selectkit-not-found")
        @ConfComments("Message when the specified kit was not found. Variables: %ARGUMENT%")
        @ConfDefault.DefaultString("&cKit &e%KIT%&c not found")
        ComponentText selectKitNotFound();

    }

    @ConfKey("no-ownership")
    @ConfComments("Message when a player does not own a kit. Variables: %KIT%")
    @ConfDefault.DefaultString("&cSorry, you do not own kit &e%KIT%&c.")
    ComponentText noOwnership();

    @ConfKey("on-cooldown")
    @ConfComments({
            "Message when a player tries to use the kit but the cooldown has not yet elapsed",
            "Variables:",
            "%COOLDOWN_REMAINING% - the relative time remaining until the kit may be used again",
            "%COOLDOWN_EXPIRATION% - the absolute date at which the kit may be used again",
            "%KIT_COOLDOWN% - the cooldown of the kit",
            "%KIT% - the kit name"})
    @ConfDefault.DefaultString("&cYou must wait &e%COOLDOWN_REMAINING% &cuntil you can use kit &e%KIT% &cagain")
    ComponentText onCooldown();

    @ConfKey("chose-kit")
    @ConfComments("Message when a player has chosen a kit. Variables: %KIT%")
    @ConfDefault.DefaultString("&7You have chosen kit &e%KIT%&7. Prepare yourself.")
    ComponentText choseKit();

    @ConfKey("kit-add-mode")
    @ConfComments({
            "ADD_OR_DROP – the kit is added to the player's inventory and extra items are dropped.",
            "COPY_AND_SET - the kit is copied to the player's inventory and replaces existing items."})
    @ConfDefault.DefaultString("ADD_OR_DROP")
    KitAddMode kitAddMode();

    enum KitAddMode {
        ADD_OR_DROP,
        COPY_AND_SET
    }

}
