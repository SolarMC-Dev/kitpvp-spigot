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
import space.arim.dazzleconf.annote.ConfHeader;
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

        @ConfKey("admin-usage")
        @ConfDefault.DefaultString("&cUsage: /kitpvp-admin <reload|createkit|deletekit>.")
        Component adminUsage();

        @ConfDefault.DefaultString("&aReloaded")
        Component reloaded();

        @ConfKey("requires-kit-name")
        @ConfDefault.DefaultString("&cKit name must be specified")
        Component requireKitName();

        @ConfKey("kit-not-found")
        @ConfComments("Message when a kit is not found. Variables: %ARGUMENT%")
        @ConfDefault.DefaultString("&cKit %ARGUMENT% not found")
        ComponentText kitNotFound();

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

    }

    @SubSection
    Bounties bounties();

    interface Bounties {

        @ConfComments({"Message when the player does not have enough money to place a bounty. Variables:",
                "%BOUNTY_ADDED% - the amount which would have been added",
                "%AVAILABLE_FUNDS% - the player's balance",
                "%BOUNTY_TARGET% - whom the bounty would be placed on"})
        @ConfDefault.DefaultString("Not enough funds to place %BOUNTY_ADDED% ON %BOUNTY_TARGET%. You have only %AVAILABLE_FUNDS%")
        ComponentText notEnoughFunds();

        @ConfKey("placed-bounty")
        @ConfComments({"Message when a player has placed or updated a bounty on another. Variables:",
                "%BOUNTY_ADDED% - the amount added",
                "%BOUNTY_NEW% - the new bounty on the target",
                "%BOUNTY_TARGET% - whom the bounty was placed on",
                ""})
        @ConfDefault.DefaultString("Placed a bounty of %BOUNTY_ADDED% on %BOUNTY_TARGET%, for a total bounty of %BOUNTY_NEW%.")
        ComponentText placedBounty();

        @ConfKey("placed-bounty-broadcast")
        @ConfComments({"Message when a player has placed or updated a bounty on another. Variables:",
                "%BOUNTY_ADDED% - the amount added",
                "%BOUNTY_NEW% - the new bounty on the target",
                "%BOUNTY_TARGET% - whom the bounty was placed on",
                "%BOUNTY_MALEFACTOR% - the player who placed the bounty ",
                ""})
        @ConfDefault.DefaultString("%BOUNTY_MALEFACTOR% placed a bounty of %BOUNTY_ADDED% on %BOUNTY_TARGET%, for a total bounty of %BOUNTY_NEW%.")
        ComponentText placedBountyBroadcast();

        @ConfKey("claimed-bounty")
        @ConfComments({"Message sent to the killer when a bounty is claimed. ",
                "Set to empty to disable. Variables:",
                "%BOUNTY_VALUE% - the claimed bounty value",
                "%BOUNTY_VALUE_EXPLICIT% - the bounty value of which was explicit due to placed bounties",
                "%BOUNTY_VALUE_IMPLICIT% - the bounty value of which was implicit due to the victim's killstreak",
                "%BOUNTY_TARGET% - whom the bounty was claimed on"})
        @ConfDefault.DefaultString("You claimed a bounty of %BOUNTY_VALUE% on %BOUNTY_TARGET%.")
        ComponentText claimedBounty();

        @ConfKey("claimed-bounty-message")
        @ConfComments({"Broadcasted message sent to everyone when a bounty is claimed. ",
                "Set to empty to disable. Variables:",
                "%BOUNTY_VALUE% - the claimed bounty value",
                "%BOUNTY_VALUE_EXPLICIT% - the bounty value of which was explicit due to placed bounties",
                "%BOUNTY_VALUE_IMPLICIT% - the bounty value of which was implicit due to the victim's killstreak",
                "%BOUNTY_TARGET% - whom the bounty was claimed on",
                "%BOUNTY_CLAIMANT% - who claimed the bounty"})
        @ConfDefault.DefaultString("%BOUNTY_CLAIMAINT% claimed a bounty of %BOUNTY_ADDED% on %BOUNTY_TARGET%.")
        ComponentText claimedBountyBroadcast();

        @ConfKey("implicit-bounties")
        @SubSection
        ImplicitBounties implicitBounties();

        @ConfHeader({
                "An implicit bounty is one which exists on a player dependent on that player's killstreak.",
                "This is not a bounty in the traditional sense, but rather a calculated additional reward",
                "given to players who kill others with a high killstreak.",
                "",
                "Internally, the implicit bounty is tracked separately from placed bounties; the implicit",
                "bounty is calculated based on the player's killstreak at the time of death.",
                "",
                "Also, if enabled, the victim with the killstreak additionally receives the same implicit bounty"
        })
        interface ImplicitBounties {

            @ConfKey("killstreak-implicit-bounties")
            @ConfComments({
                    "Defines the implicit bounty which exists on players with the given killstreaks.",
            })
            @ConfDefault.DefaultMap({
                    "50", "1500",
                    "100", "3000",
                    "150", "5000",
                    "200", "7000",
                    "250", "10000"
            })
            RangedLookupTableBigDecimal killstreakImplicitBounties();

            @ConfKey("victim-receives-implicit-bounty")
            @ConfComments("When enabled, the victim also receives any implicit bounty which existed due to their high killstreak")
            @ConfDefault.DefaultBoolean(true)
            boolean victimReceivesImplicitBounty();

            @ConfKey("implicit-bounty-message-killer")
            @ConfComments({"The message to the killer when they receive the implicit bounty.",
                    "Usually, you can disable this message and use the claimed-bounty-message.",
                    "Set to empty to disable. Variables:",
                    "%IMPLICIT_BOUNTY% - the value claimed through the implicit bounty",
                    "%VICTIM% - the name of the victim",
                    "%VICTIM_KILLSTREAK% - the killstreak the victim had"})
            @ConfDefault.DefaultString("You gained an additional reward of %IMPLICIT_BOUNTY% " +
                    "for killing %VICTIM% who had a %VICTIM_KILLSTREAK% killstreak")
            ComponentText implicitBountyMessageKiller();

            @ConfKey("implicit-bounty-message-victim")
            @ConfComments({"The message to a victim when they receive the implicit bounty.",
                    "Set to empty to disable. Variables:",
                    "%IMPLICIT_BOUNTY% - the value claimed through the implicit bounty",
                    "%KILLER% - the name of the killer",
                    "%VICTIM_KILLSTREAK% - the killstreak the victim had"})
            @ConfDefault.DefaultString("You gained an implicit bounty of %IMPLICIT_BOUNTY% f" +
                    "or achieving such a high killstreak of %VICTIM_KILLSTREAK%.")
            ComponentText implicitBountyMessageVictim();

        }

        @SubSection
        Commands commands();

        interface Commands {

            @ConfKey("usage")
            @ConfDefault.DefaultString("&cUsage: /bounty <add|view> <player>")
            Component usage();

            @ConfKey("usage-add")
            @ConfDefault.DefaultString("&cUsage: /bounty add <player> <number>")
            Component usageAdd();

            @ConfKey("usage-view")
            @ConfDefault.DefaultString("&cUsage: /bounty view <player>")
            Component usageView();

            @ConfKey("add-not-a-number")
            @ConfComments("Message when input is not a number. Variables: %ARGUMENT%")
            @ConfDefault.DefaultString("&cUsage: /bounty add <player> <number>. Must specify a valid number.")
            ComponentText addNotANumber();

            @ConfKey("player-not-found")
            @ConfComments("Message when player is not found. Variables: %ARGUMENT%")
            @ConfDefault.DefaultString("&cPlayer &e%ARGUMENT%&c was not found.")
            ComponentText playerNotFound();

            @ConfKey("view-message")
            @ConfComments({"/bounty view display. Variables:",
                    "%TARGET% - the target player",
                    "%BOUNTY_VALUE% - the bounty value"})
            @ConfDefault.DefaultString("&7The bounty on &e%TARGET%&7 is &5%BOUNTY_VALUE%&7.")
            ComponentText viewMessage();

            @ConfKey("view-message-no-bounty")
            @ConfComments({"/bounty view display when there is no bounty. Variables:",
                    "%TARGET% - the target player"})
            @ConfDefault.DefaultString("&7There is no bounty on &e%TARGET%&7.")
            ComponentText viewMessageNoBounty();

        }

    }

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
