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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LevelCalculatorTest {

    private final ConfigCenter configCenter;
    private LevelCalculator levelCalculator;

    public LevelCalculatorTest(@Mock ConfigCenter configCenter) {
        this.configCenter = configCenter;
    }

    @BeforeEach
    public void setLevelCalculator() {
        levelCalculator = new LevelCalculator(configCenter);
    }

    @Test
    public void calculateLevel() {
        assertEquals(1, levelCalculator.calculateLevel(0));
        assertEquals(2, levelCalculator.calculateLevel(1));
        assertEquals(3, levelCalculator.calculateLevel(3));
        assertEquals(4, levelCalculator.calculateLevel(6));
        assertEquals(5, levelCalculator.calculateLevel(10));
    }

}
