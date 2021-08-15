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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.arim.omnibus.util.ThisClass;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class LoggingFuturePoster implements FuturePoster {

    private static final Logger LOGGER = LoggerFactory.getLogger(ThisClass.get());

    @Override
    public void postFuture(CentralisedFuture<?> future) {
        future.exceptionally((ex) -> {
            LOGGER.warn("Encountered miscellaneous exception", ex);
            return null;
        }).orTimeout(10L, TimeUnit.SECONDS).exceptionally((ex) -> {
            LOGGER.warn("Future took more than 10 seconds to complete", ex);
            return null;
        });
    }
}
