package gg.solarmc.kitpvp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public class Util {

    private final static Logger logger = LoggerFactory.getLogger(Util.class);
    public final static BiConsumer<? super Object, ? super Throwable> FUTURE_LOG = (s,e) -> {
        if (e != null) {
            logger.error("Error found when executing action: " + e);
        }
    };

    public static <T> BiConsumer<? super T,? super Throwable> check() {
        return (s,e) -> {

        };
    }

}
