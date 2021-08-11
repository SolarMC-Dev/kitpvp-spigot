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

import gg.solarmc.kitpvp.config.Bounties;
import gg.solarmc.kitpvp.config.Config;
import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.loader.kitpvp.BountyAmount;
import gg.solarmc.loader.kitpvp.BountyCurrency;
import gg.solarmc.loader.kitpvp.Kit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import space.arim.api.jsonchat.adventure.util.ComponentText;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FormatterTest {

    private final ConfigCenter configCenter;
    private Formatter formatter;

    public FormatterTest(@Mock ConfigCenter configCenter) {
        this.configCenter = configCenter;
    }

    @BeforeEach
    public void setFormatter() {
        formatter = new Formatter(configCenter);
    }

    @Test
    public void formatAbsoluteDate() {
        Instant date = Instant.parse("2021-08-01T04:55:05+00:00");
        assertEquals("August 1, 2021, 4:55:05 AM", formatter.formatAbsoluteDate(date));
    }

    @Test
    public void formatRemainingCooldown(@Mock Kit kit) {
        Duration cooldownRemaining = Duration.ofDays(2L).plus(Duration.ofHours(5L)).plus(Duration.ofMinutes(3L).plus(Duration.ofSeconds(14L)));
        when(kit.getCooldown()).thenReturn(Duration.ofDays(3L));
        assertEquals("2 days, 5 hours, 3 minutes, 14 seconds", formatter.formatRemainingCooldown(kit, cooldownRemaining));
    }

    @Test
    public void twentyThreeHoursFiftyNineMinutesProblem(@Mock Kit kit) {
        Duration cooldownRemaining = Duration.ofDays(3L).minus(Duration.ofSeconds(1L));
        when(kit.getCooldown()).thenReturn(Duration.ofDays(3L));
        assertEquals("3 days", formatter.formatRemainingCooldown(kit, cooldownRemaining));
    }

    private void setBountyFormatConfig(Set<BountyCurrency> currencies) {
        Config config = mock(Config.class);
        Bounties bounties = mock(Bounties.class);
        Bounties.CurrencyDisplay currencyDisplay = mock(Bounties.CurrencyDisplay.class);
        when(configCenter.config()).thenReturn(config);
        when(config.bounties()).thenReturn(bounties);
        when(bounties.currencyDisplay()).thenReturn(currencyDisplay);
        Map<BountyCurrency, ComponentText> formattedValue = new EnumMap<>(BountyCurrency.class);
        for (BountyCurrency currency : currencies) {
            formattedValue.put(currency, ComponentText.create(Component.text("%VALUE% " + currency.name().toLowerCase(Locale.ROOT))));
        }
        when(currencyDisplay.formattedValue()).thenReturn(formattedValue);
    }

    @Test
    public void formatBounty() {
        setBountyFormatConfig(Set.of(BountyCurrency.CREDITS));
        Component sourceComponent = Component.text("The bounty is %BOUNTY_VALUE%");
        BountyAmount amount = BountyCurrency.CREDITS.createAmount(BigDecimal.TEN);
        Component replaced = sourceComponent.replaceText(formatter.formatBounty("%BOUNTY_VALUE%", amount));
        assertEquals("The bounty is 10 credits", PlainComponentSerializer.plain().serialize(replaced));
    }

    @Test
    public void formatBounties() {
        setBountyFormatConfig(Set.of(BountyCurrency.values()));
        Component sourceComponent = Component.text("The bounty is %BOUNTY_VALUE_CREDITS% / %BOUNTY_VALUE_PLAIN_ECO%");
        Map<BountyCurrency, BigDecimal> bounties = Map.of(
                BountyCurrency.CREDITS, BigDecimal.TEN, BountyCurrency.PLAIN_ECO, BigDecimal.valueOf(100));
        Component replaced = sourceComponent.replaceText(formatter.formatBounties("BOUNTY_VALUE", bounties));
        assertEquals("The bounty is 10 credits / 100 plain_eco", PlainComponentSerializer.plain().serialize(replaced));
    }
}
