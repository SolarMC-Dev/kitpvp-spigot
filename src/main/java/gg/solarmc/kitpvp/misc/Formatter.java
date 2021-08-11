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

import gg.solarmc.kitpvp.config.ConfigCenter;
import gg.solarmc.loader.kitpvp.BountyAmount;
import gg.solarmc.loader.kitpvp.BountyCurrency;
import gg.solarmc.loader.kitpvp.Kit;
import jakarta.inject.Inject;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import space.arim.api.jsonchat.adventure.util.ComponentText;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class Formatter {

    private final ConfigCenter configCenter;
    private final DateTimeFormatter timeFormatter;

    public Formatter(ConfigCenter configCenter, DateTimeFormatter timeFormatter) {
        this.configCenter = configCenter;
        this.timeFormatter = timeFormatter;
    }

    @Inject
    public Formatter(ConfigCenter configCenter) {
        this(configCenter, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM).withLocale(Locale.ENGLISH));
    }

    public CharSequence formatAbsoluteDate(Instant date) {
        return timeFormatter.format(date.atZone(ZoneOffset.UTC));
    }

    public CharSequence formatCooldown(Kit kit) {
        return formatTimespan(kit.getCooldown());
    }

    public CharSequence formatRemainingCooldown(Kit kit, Duration remainingCooldown) {
        // Prevent the 23 hours 59 minutes problem
        Duration cooldown = kit.getCooldown();
        if (kit.getCooldown().minus(remainingCooldown).compareTo(Duration.ofMinutes(1L)) < 0) {
            return formatTimespan(cooldown);
        }
       return formatTimespan(remainingCooldown);
    }

    private static final List<Map.Entry<Duration, String>> UNITS_AND_SUFFIXES = List.of(
            Map.entry(Duration.ofDays(1L), "days"),
            Map.entry(Duration.ofHours(1L), "hours"),
            Map.entry(Duration.ofMinutes(1L), "minutes"),
            Map.entry(Duration.ofSeconds(1L), "seconds")
    );

    private String formatTimespan(Duration timespan) {
        StringBuilder builder = new StringBuilder();
        timespan = timespan.truncatedTo(ChronoUnit.SECONDS);
        if (timespan.isZero()) {
            return "1 second";
        }
        for (Map.Entry<Duration, String> entry : UNITS_AND_SUFFIXES) {
            Duration unit = entry.getKey();
            if (timespan.compareTo(unit) > 0) {
                long unitValue = timespan.dividedBy(unit);
                builder.append(", ").append(unitValue).append(' ').append(entry.getValue());
                timespan = timespan.minus(unit.multipliedBy(unitValue));
            }
        }
        return builder.substring(2);
    }

    public CharSequence formatCurrency(BountyCurrency currency) {
        return configCenter.config().bounties().currencyDisplay().currencyName().getOrDefault(currency, "unconfigured");
    }

    private ComponentLike formatBountyImmediate(BountyAmount bounty) {
        ComponentText format = configCenter.config().bounties().currencyDisplay().formattedValue().get(bounty.currency());
        if (format == null) {
            format = ComponentText.create(Component.text("unconfigured"));
        }
        return format.replaceText("%VALUE%", bounty.value().toPlainString());
    }

    /**
     * Configures text replacement to display a bounty
     *
     * @param variable the bounty variable to replace
     * @param bounty the bounty
     * @return the text replacement configurer
     */
    public Consumer<TextReplacementConfig.Builder> formatBounty(String variable, BountyAmount bounty) {
        return (builder) -> {
            builder.matchLiteral(variable).replacement(formatBountyImmediate(bounty));
        };
    }

    /**
     * Configures text replacement to display multiple bounties
     *
     * @param variablePrefix the prefix of the variable for each bounty, for example, "BOUNTY_VALUE"
     *                       will correspond to variables such as {@literal "%BOUNTY_VALUE_<currency>%"}
     * @param bounties the bounties to display
     * @return the text replacement configurer
     */
    public Consumer<TextReplacementConfig.Builder> formatBounties(String variablePrefix, Map<BountyCurrency, BigDecimal> bounties) {
        return (builder) -> {
            StringBuilder pattern = new StringBuilder();
            for (BountyCurrency currency : bounties.keySet()) {
                String patternForBounty = '(' + Pattern.quote('%' + variablePrefix + '_' + currency.name() + '%') + ')';
                if (!pattern.isEmpty()) {
                    pattern.append('|');
                }
                pattern.append(patternForBounty);
            }
            builder.match(pattern.toString());
            builder.replacement((groupComponent) -> {
                String matchedGroup = groupComponent.content();
                // Extract currency from %variableprefix_<currency>%
                String currencyString = matchedGroup.substring(2 + variablePrefix.length(), matchedGroup.length() - 1);
                BountyCurrency currency = BountyCurrency.valueOf(currencyString);
                return formatBountyImmediate(currency.createAmount(bounties.get(currency)));
            });
        };
    }

}
