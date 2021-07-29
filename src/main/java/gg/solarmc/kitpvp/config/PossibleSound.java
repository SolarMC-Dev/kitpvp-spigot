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

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.Map;
import java.util.Optional;

public interface PossibleSound {

    Optional<Sound> sound();

    default void playTo(Player player) {
        sound().ifPresent(player::playSound);
    }

    class SoundSerializer implements ValueSerialiser<PossibleSound> {

        @Override
        public Class<PossibleSound> getTargetClass() {
            return PossibleSound.class;
        }

        @Override
        public PossibleSound deserialise(FlexibleType flexibleType) throws BadValueException {
            Map<String, FlexibleType> map = flexibleType.getMap((key, value) -> Map.entry(key.getString(), value));
            if (map.isEmpty()) {
                return Optional::empty;
            }
            Sound sound = Sound.sound(
                    Key.key("minecraft", getMapValue(flexibleType, map, "name").getString()),
                    getMapValue(flexibleType, map, "source").getEnum(Sound.Source.class),
                    getMapValue(flexibleType, map, "volume").getFloat(),
                    getMapValue(flexibleType, map, "pitch").getFloat());
            return () -> Optional.of(sound);
        }

        private FlexibleType getMapValue(FlexibleType exceptionHelper,
                                         Map<String, FlexibleType> map, String attribute) throws BadValueException {
            FlexibleType source = map.get(attribute);
            if (source == null) {
                throw exceptionHelper.badValueExceptionBuilder()
                        .message("Sound " + attribute + " not specified").build();
            }
            return source;
        }

        @Override
        public Object serialise(PossibleSound value, Decomposer decomposer) {
            Optional<Sound> optSound = value.sound();
            if (optSound.isEmpty()) {
                return Map.of();
            }
            Sound sound = optSound.get();
            return Map.of(
                    "name", sound.name().value(),
                    "source", decomposer.decompose(Sound.Source.class, sound.source()),
                    "volume", sound.volume(),
                    "pitch", sound.pitch());
        }

    }
}
