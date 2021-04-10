package gg.solarmc.kitpvp.kill;

import java.util.UUID;

/**
 * Represents something that **TYPICALLY** stores a STRONG REFERENCED OBJECT that can close on a player logout
 * and remove all references to get rid of memory leeks
 *
 * unFORTUNATELY in this case SOMEONE told me to refactor everything to use UUIDS so now this is just a weird
 * vestigial class that still kinda has a point ( i mean leaving uuids in memory post logout isn't bad now is it?)
 *
 *
 */
public interface DamageClosable {

    void removeHolder(UUID uuid);

}
