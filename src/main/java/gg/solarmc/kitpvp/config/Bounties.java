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

import gg.solarmc.loader.kitpvp.BountyCurrency;
import net.kyori.adventure.text.Component;
import space.arim.api.jsonchat.adventure.util.ComponentText;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfHeader;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@ConfHeader({
        "All configuration related to bounties",
        "",
        "Note that when a variable includes <currency>, the currency is replaced",
        "with either CREDITS or PLAIN_ECO. For example, %BOUNTY_VALUE_<currency>% means",
        "%BOUNTY_VALUE_CREDITS% and %BOUNTY_VALUE_PLAIN_ECO% are both available."
})
public interface Bounties {

    @ConfKey("currency-display")
    @SubSection
    CurrencyDisplay currencyDisplay();

    interface CurrencyDisplay {

        @ConfKey("currency-name")
        @ConfComments("How should the currency itself be displayed? This becomes the value of %BOUNTY_CURRENCY%")
        @ConfDefault.DefaultMap({"CREDITS", "credits", "PLAIN_ECO", "economy"})
        Map<BountyCurrency, String> currencyName();

        @ConfKey("formatted-value")
        @ConfComments({"How a value in a certain currency should be displayed. Color codes are allowed.",
                "Variables: %VALUE% - the literal value"})
        @ConfDefault.DefaultMap({"CREDITS", "&a$&6%VALUE%", "PLAIN_ECO", "&a$&7%VALUE%"})
        Map<BountyCurrency, ComponentText> formattedValue();

        @ConfKey("currency-argument")
        @ConfComments("Defines which values are accepted as currency arguments inside commands")
        @ConfDefault.DefaultObject("defaultCurrencyArgument")
        Map<BountyCurrency, @SubSection AcceptedArguments> currencyArgument();

        static Map<BountyCurrency, @SubSection AcceptedArguments> defaultCurrencyArgument() {
            return Map.of(BountyCurrency.CREDITS, new AcceptedArguments.FromExisting(Set.of("credits")),
                    BountyCurrency.PLAIN_ECO, new AcceptedArguments.FromExisting(Set.of("eco", "economy")));
        }

        interface AcceptedArguments {

            Set<String> acceptedArguments();

            record FromExisting(Set<String> acceptedArguments) implements AcceptedArguments {}
        }
    }

    @ConfComments({"Message when the player does not have enough money to place a bounty. Variables:",
            "%BOUNTY_CURRENCY% - the bounty currency",
            "%BOUNTY_ADDED% - the amount which would have been added",
            "%AVAILABLE_FUNDS% - the player's balance",
            "%BOUNTY_TARGET% - whom the bounty would be placed on"})
    @ConfDefault.DefaultString("Not enough funds in %BOUNTY_CURRENCY% to place %BOUNTY_ADDED% ON %BOUNTY_TARGET%. You have only %AVAILABLE_FUNDS%")
    ComponentText notEnoughFunds();

    @ConfKey("placed-bounty")
    @ConfComments({"Message when a player has placed or updated a bounty on another. Variables:",
            "%BOUNTY_CURRENCY% - the bounty currency",
            "%BOUNTY_ADDED% - the amount added",
            "%BOUNTY_NEW% - the new bounty on the target",
            "%BOUNTY_TARGET% - whom the bounty was placed on",
            ""})
    @ConfDefault.DefaultString("Placed a bounty of %BOUNTY_ADDED% in %BOUNTY_CURRENCY% on %BOUNTY_TARGET%, for a total bounty of %BOUNTY_NEW%.")
    ComponentText placedBounty();

    @ConfKey("placed-bounty-broadcast")
    @ConfComments({"Message when a player has placed or updated a bounty on another. Variables:",
            "%BOUNTY_CURRENCY% - the bounty currency",
            "%BOUNTY_ADDED% - the amount added",
            "%BOUNTY_NEW% - the new bounty on the target",
            "%BOUNTY_TARGET% - whom the bounty was placed on",
            "%BOUNTY_MALEFACTOR% - the player who placed the bounty ",
            ""})
    @ConfDefault.DefaultString("%BOUNTY_MALEFACTOR% placed a bounty of %BOUNTY_ADDED% in %BOUNTY_CURRENCY% on %BOUNTY_TARGET%, for a total bounty of %BOUNTY_NEW%.")
    ComponentText placedBountyBroadcast();

    @ConfKey("claimed-bounty")
    @ConfComments({"Message sent to the killer when a bounty is claimed. ",
            "Set to empty to disable. Variables:",
            "%BOUNTY_CURRENCY% - the bounty currency",
            "%BOUNTY_VALUE% - the claimed bounty value",
            "%BOUNTY_VALUE_EXPLICIT% - the bounty value of which was explicit due to placed bounties",
            "%BOUNTY_VALUE_IMPLICIT% - the bounty value of which was implicit due to the victim's killstreak",
            "%BOUNTY_TARGET% - whom the bounty was claimed on"})
    @ConfDefault.DefaultString("You claimed a bounty of %BOUNTY_VALUE% in %BOUNTY_CURRENCY% on %BOUNTY_TARGET%.")
    ComponentText claimedBounty();

    @ConfKey("claimed-bounty-message")
    @ConfComments({"Broadcasted message sent to everyone when a bounty is claimed. ",
            "Set to empty to disable. Variables:",
            "%BOUNTY_CURRENCY% - the bounty currency",
            "%BOUNTY_VALUE% - the claimed bounty value",
            "%BOUNTY_VALUE_EXPLICIT% - the bounty value of which was explicit due to placed bounties",
            "%BOUNTY_VALUE_IMPLICIT% - the bounty value of which was implicit due to the victim's killstreak",
            "%BOUNTY_TARGET% - whom the bounty was claimed on",
            "%BOUNTY_CLAIMANT% - who claimed the bounty"})
    @ConfDefault.DefaultString("%BOUNTY_CLAIMANT% claimed a bounty of %BOUNTY_ADDED% in %BOUNTY_CURRENCY% on %BOUNTY_TARGET%.")
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

        @ConfKey("killstreak-implicit-bounties-per-currency")
        @ConfComments({
                "Defines the implicit bounty which exists on players with the given killstreaks.",
        })
        @ConfDefault.DefaultObject("defaultKillstreakImplicitBountiesPerCurrency")
        Map<BountyCurrency, RangedLookupTableBigDecimal> killstreakImplicitBountiesPerCurrency();

        static Map<BountyCurrency, RangedLookupTableBigDecimal> defaultKillstreakImplicitBountiesPerCurrency() {
            var serializer = new RangedLookupTableBigDecimal.Serializer();
            return Map.of(
                    BountyCurrency.CREDITS, serializer.fromValueRanges(Map.of(
                            200, BigDecimal.TEN
                    )),
                    BountyCurrency.PLAIN_ECO, serializer.fromValueRanges(Map.of(
                            50, BigDecimal.valueOf(1500),
                            100, BigDecimal.valueOf(3000),
                            150, BigDecimal.valueOf(5000),
                            200, BigDecimal.valueOf(7000),
                            250, BigDecimal.valueOf(10000)
                    ))
            );
        }

        @ConfKey("victim-receives-implicit-bounty")
        @ConfComments("When enabled, the victim also receives any implicit bounty which existed due to their high killstreak")
        @ConfDefault.DefaultBoolean(true)
        boolean victimReceivesImplicitBounty();

        @ConfKey("implicit-bounty-message-killer")
        @ConfComments({"The message to the killer when they receive the implicit bounty.",
                "Usually, you can disable this message and use the claimed-bounty-message.",
                "Set to empty to disable. Variables:",
                "%BOUNTY_CURRENCY% - the bounty currency",
                "%IMPLICIT_BOUNTY% - the value claimed through the implicit bounty",
                "%VICTIM% - the name of the victim",
                "%VICTIM_KILLSTREAK% - the killstreak the victim had"})
        @ConfDefault.DefaultString("You gained an additional reward of %IMPLICIT_BOUNTY% in %BOUNTY_CURRENCY% " +
                "for killing %VICTIM% who had a %VICTIM_KILLSTREAK% killstreak")
        ComponentText implicitBountyMessageKiller();

        @ConfKey("implicit-bounty-message-victim")
        @ConfComments({"The message to a victim when they receive the implicit bounty.",
                "Set to empty to disable. Variables:",
                "%BOUNTY_CURRENCY% - the bounty currency",
                "%IMPLICIT_BOUNTY% - the value claimed through the implicit bounty",
                "%KILLER% - the name of the killer",
                "%VICTIM_KILLSTREAK% - the killstreak the victim had"})
        @ConfDefault.DefaultString("You gained an implicit bounty of %IMPLICIT_BOUNTY% in %BOUNTY_CURRENCY% " +
                "for achieving such a high killstreak of %VICTIM_KILLSTREAK%.")
        ComponentText implicitBountyMessageVictim();

    }

    @SubSection
    Commands commands();

    interface Commands {

        @ConfKey("usage")
        @ConfDefault.DefaultString("&cUsage: /bounty <add|view|list> ...")
        Component usage();

        @ConfKey("usage-add")
        @ConfDefault.DefaultString("&cUsage: /bounty add <player> <currency> <number>")
        Component usageAdd();

        @ConfKey("usage-view")
        @ConfDefault.DefaultString("&cUsage: /bounty view <player>")
        Component usageView();

        @ConfKey("not-a-currency")
        @ConfComments("Message when invalid currency specified. Variables: %ARGUMENT%")
        @ConfDefault.DefaultString("&cArgument %ARGUMENT% must be a valid currency")
        ComponentText notACurrency();

        @ConfKey("add-not-a-number")
        @ConfComments("Message when input is not a number. Variables: %ARGUMENT%")
        @ConfDefault.DefaultString("&cUsage: /bounty add <player> <number>. Must specify a valid number.")
        ComponentText addNotANumber();

        @ConfKey("view-message")
        @ConfComments({"/bounty view display. Variables:",
                "%TARGET% - the target player",
                "%BOUNTY_VALUE_<currency>% - the bounty value"})
        @ConfDefault.DefaultString("&7The bounty on &e%TARGET%&7 is %BOUNTY_VALUE_CREDITS% and %BOUNTY_VALUE_PLAIN_ECO%&7.")
        ComponentText viewMessage();

        @ConfKey("bounty-list-page")
        @ConfComments({"Display for a page of the bounties. Variables: ",
                "%BOUNTIES_ON_PAGE% - concatenated list of bounties on this page",
                "%NEXT_PAGE% - the message regarding the next page. If it is known that no more pages are present, this is empty."})
        @ConfDefault.DefaultString("""
                &eBounties
                %BOUNTIES_ON_PAGE%||
                %NEXT_PAGE%
                """)
        ComponentText bountyListPage();

        @ConfKey("bounty-list-next-page")
        @ConfComments({
                "Message to navigate to the next page.",
                "This is the value of %NEXT_PAGE% in bounty-list-page when more pages may be present",
                "Variables:,",
                "%NEXT_PAGE_CODE% - code to visit the next page. Not normally visible to users."})
        @ConfDefault.DefaultString("&7Click to visit the next page.||cmd:/bounty list %NEXT_PAGE_CODE%")
        ComponentText bountyListNextPage();

        @ConfKey("bounty-list-nopages")
        @ConfComments("Message when no more pages are present.")
        @ConfDefault.DefaultString("&cNo more pages exist.")
        Component bountyListNoPages();

        @ConfKey("bounties-per-page")
        @ConfComments("Amount of bounties shown per page")
        @ConfDefault.DefaultInteger(5)
        int bountiesPerPage();

        @ConfKey("bounty-list-bounty")
        @ConfComments({"Display for an individual bounty on the page.",
                "Variables: %TARGET%, %BOUNTY_VALUE_<currency>%"})
        @ConfDefault.DefaultString("%BOUNTY_TARGET% - %BOUNTY_VALUE_CREDITS% and %BOUNTY_VALUE_PLAIN_ECO%")
        ComponentText bountyListBounty();

        @ConfKey("bounty-list-invalid-pagecode")
        @ConfComments("This can happen if the user scrolls way up in their chat and clicks the next page button on an old bounty list message")
        @ConfDefault.DefaultString("&cInvalid page code %ARGUMENT%")
        ComponentText bountyListInvalidPagecode();

    }

}
