package gg.solarmc.kitpvp;

import org.bukkit.command.Command;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

public interface KitpvpConfig {

    @ConfComments("How much money you get for killing")
    @ConfKey("rewards.kill")
    @ConfDefault.DefaultInteger(50)
    int killMoney();

    @ConfKey("rewards.kill_addition")
    @ConfComments("The amount of money you get extra for killing")
    @ConfDefault.DefaultInteger(10)
    int killAddition();

    @ConfComments("Levels between additional reward increase")
    @ConfKey("rewards.kill_addition_interval")
    @ConfDefault.DefaultInteger(50)
    int killAdditionInterval();

    @ConfKey("rewards.assist")
    @ConfDefault.DefaultInteger(20)
    int assistMoney();

}
