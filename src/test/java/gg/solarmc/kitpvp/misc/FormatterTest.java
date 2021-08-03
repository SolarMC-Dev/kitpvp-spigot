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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FormatterTest {

    private final Formatter formatter = new Formatter();

    @Test
    public void formatAbsoluteDate() {
        Instant date = Instant.parse("2021-08-01T04:55:05+00:00");
        assertEquals("August 1, 2021, 4:55:05 AM", formatter.formatAbsoluteDate(date));
    }

    @Test
    public void formatRemainingCooldown(@Mock Kit kit) {
        Duration cooldownRemaining = Duration.ofDays(2L).plus(Duration.ofHours(5L)).plus(Duration.ofMinutes(3L));
        when(kit.getCooldown()).thenReturn(Duration.ofDays(3L));
        assertEquals("2 days, 5 hours, 3 minutes", formatter.formatRemainingCooldown(kit, cooldownRemaining));
    }

    @Test
    public void twentyThreeHoursFiftyNineMinutesProblem(@Mock Kit kit) {
        Duration cooldownRemaining = Duration.ofDays(3L).minus(Duration.ofSeconds(1L));
        when(kit.getCooldown()).thenReturn(Duration.ofDays(3L));
        assertEquals("3 days", formatter.formatRemainingCooldown(kit, cooldownRemaining));
    }
}
