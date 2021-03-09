package gg.solar.kitpvp;

import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;

public interface KitpvpConfig {

    @ConfKey("rewards.kill")
    @ConfDefault.DefaultInteger(50)
    int killMoney();

    @ConfKey("rewards.assist")
    @ConfDefault.DefaultInteger(20)
    int assistMoney();

}
