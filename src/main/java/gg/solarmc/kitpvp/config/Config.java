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

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import space.arim.api.jsonchat.adventure.util.ComponentText;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.Optional;

public interface Config {

    @ConfComments("All violent action, plain as it is, and its immediate consequences")
    @SubSection
    Violence violence();

    @ConfKey("kits")
    @SubSection
    KitConfig kitConfig();

    @ConfKey("misc-messages")
    @SubSection
    Messages messages();

    interface Messages {

        @ConfKey("no-permission")
        @ConfDefault.DefaultString("&cSorry, you cannot use this.")
        Component noPermission();

        @ConfKey("player-not-found")
        @ConfComments("Message when player is not found. Variables: %ARGUMENT%")
        @ConfDefault.DefaultString("&cPlayer &e%ARGUMENT%&c was not found.")
        ComponentText playerNotFound();

        @ConfKey("page-does-not-exist")
        @ConfComments("Message when the given page does not exist. Variables: %ARGUMENT%")
        @ConfDefault.DefaultString("&cPage %ARGUMENT% does not exist.")
        ComponentText pageDoesNotExist();

        @ConfKey("admin-usage")
        @ConfDefault.DefaultString("&cUsage: /kitpvp-admin <reload|createkit|deletekit>.")
        Component adminUsage();

        @ConfDefault.DefaultString("&aReloaded")
        Component reloaded();

        @ConfKey("requires-kit-name")
        @ConfComments("Message for createkit and deletekit when kit name not specified")
        @ConfDefault.DefaultString("&cKit name must be specified")
        Component requireKitName();

        @ConfKey("kit-not-found")
        @ConfComments("Message when a kit is not found. Variables: %ARGUMENT%")
        @ConfDefault.DefaultString("&cKit %ARGUMENT% not found")
        ComponentText kitNotFound();

        @ConfKey("invalid-cooldown")
        @ConfComments({"Message when a cooldown argument is not a valid timespan. ",
                "Parsing relies on Duration.parse which uses the ISO-8601 format",
                "Variables: %ARGUMENT%"})
        @ConfDefault.DefaultString("Specified cooldown %ARGUMENT% is not a valid ISO-8601 timespan.")
        ComponentText invalidCooldown();

        @ConfKey("delete-kit-success")
        @ConfComments("Message when a kit is deleted. Variable: %KIT%")
        @ConfDefault.DefaultString("Kit %KIT% deleted")
        ComponentText deleteKitSuccess();

        @ConfKey("create-kit-success")
        @ConfComments("Message when a kit is created. Variables: %KIT%")
        @ConfDefault.DefaultString("Created kit %KIT% with the contents of your inventory")
        ComponentText createKitSuccess();

        @ConfKey("create-kit-already-exists")
        @ConfComments("Message when a kit already exists with that name. Variables: %KIT%")
        @ConfDefault.DefaultString("Unable to create %KIT% because it already exists")
        ComponentText createKitAlreadyExists();

        @ConfKey("stats-success")
        @ConfComments({"Stats command message",
                "Variables:",
                "%TARGET% - the user whose stats is shown",
                "%KILLS%, %DEATHS%, %ASSISTS%, %KDR% - self-explanatory",
                "%KILLSTREAK%, %HIGHEST_KILLSTREAK% - current killstreak and highest killstreak",
                "%EXPERIENCE% - the raw experience",
                "%LEVEL% - the level",
                "%FORMATTED_LEVEL% - the level formatted as it would be in the chat"})
        @ConfDefault.DefaultString(
                """
                        &7Statistics for &e%TARGET%
                        Kills, deaths, assists, kdr: %KILLS%, %DEATHS%, %ASSISTS%, %KDR%
                        Killstreak, Highest killstreak: %KILLSTREAK%, %HIGHEST_KILLSTREAK%
                        Level: %FORMATTED_LEVEL%
                        """
        )
        ComponentText statsSuccess();

    }

    @SubSection
    Bounties bounties();

    @SubSection
    Levels levels();

    interface Levels {

        @ConfKey("level-formatting-table")
        @ConfDefault.DefaultMap({
                "1", "&7%LEVEL%",
                "6", "&a%LEVEL%",
                "11", "&e%LEVEL%",
                "16", "&b%LEVEL%",
                "21", "&6%LEVEL%",
                "26", "&5%LEVEL%",
                "31", "&f%LEVEL%",
                "36", "&d%LEVEL%",
                "41", "&c%LEVEL%",
                "46", "&4%LEVEL%",
                "50", "&6&l%LEVEL%",
        })
        RangedLookupTableComponentText levelFormatting();

        @ConfKey("level-up-message")
        @ConfComments({"The message when a player levels up. Set to empty to disable. Variables:",
                "%OLD_LEVEL% - the old level",
                "%NEW_LEVEL% - the new level",
                "Note that these variables are colored according to the level-formatting-table setting"})
        @ConfDefault.DefaultString("&3You reached level %NEW_LEVEL%")
        ComponentText levelUpMessage();

        @ConfKey("level-up-sound")
        @ConfComments("The sound when a player levels up. Set to empty ({}) to disable")
        @ConfDefault.DefaultObject("defaultLevelUpSound")
        PossibleSound levelUpSound();

        static PossibleSound defaultLevelUpSound() {
            return () -> Optional.of(Sound.sound(
                    org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, Sound.Source.PLAYER, 1F, 1F));
        }

    }

}
