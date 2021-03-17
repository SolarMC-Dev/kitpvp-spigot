package gg.solarmc.kitpvp;

import gg.solarmc.loader.kitpvp.ItemSerializer;
import gg.solarmc.loader.kitpvp.KitItem;

public class KitSerializationSpigot implements ItemSerializer {


    @Override
    public byte[] serialize(KitItem<?> kitItem) {
        return new byte[0];
    }

    @Override
    public KitItem<?> deserialize(byte[] bytes) {
        return null;
    }
}
