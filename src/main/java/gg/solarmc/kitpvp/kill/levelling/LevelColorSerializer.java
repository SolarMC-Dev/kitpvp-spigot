package gg.solarmc.kitpvp.kill.levelling;

import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;


public class LevelColorSerializer implements ValueSerialiser<LevelColor> {

    @Override
    public Class<LevelColor> getTargetClass() {
        return LevelColor.class;
    }

    @Override
    public LevelColor deserialise(FlexibleType flexibleType) throws BadValueException {
        String[] parts = flexibleType.getString().split("-");

        if (parts.length != 3) throw new BadValueException.Builder().message("Parts cannot be not three").build();
        try {
            int lower = Integer.parseInt(parts[0]);
            int upper = Integer.parseInt(parts[1]);
            String message = parts[2];

            return new LevelColor(lower,upper,message);

        } catch (NumberFormatException e) {
            throw new BadValueException.Builder().cause(e).build();
        }
    }

    @Override
    public String serialise(LevelColor levelColor, Decomposer decomposer) {
        return levelColor.getLowerBound() + "-" + levelColor.getUpperBound() + "-" + levelColor.getColoredString();
    }

    boolean isInt(String string) {
        try {
            Integer.parseInt(string);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
