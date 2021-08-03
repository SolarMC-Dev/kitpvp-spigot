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

import gg.solarmc.loader.kitpvp.Kit;
import jakarta.inject.Inject;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Formatter {

    private final DateTimeFormatter timeFormatter;

    public Formatter(DateTimeFormatter timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    @Inject
    public Formatter() {
        this(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.MEDIUM).withLocale(Locale.ENGLISH));
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
            Map.entry(Duration.ofMinutes(1L), "minutes")
    );

    private String formatTimespan(Duration timespan) {
        StringBuilder builder = new StringBuilder();
        timespan = timespan.truncatedTo(ChronoUnit.MINUTES);
        if (timespan.isZero()) {
            return "1 minute";
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

}
