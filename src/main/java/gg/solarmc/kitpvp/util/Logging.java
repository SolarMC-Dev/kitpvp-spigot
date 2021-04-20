package gg.solarmc.kitpvp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiConsumer;

public class Logging implements BiConsumer<Object, Throwable> {

    private final static Logger logger = LoggerFactory.getLogger(Logging.class);
    public final static Logging INSTANCE = new Logging();

    @Override
    public void accept(Object o, Throwable throwable) {
        if (throwable != null) {
            logger.error("Error occurred during future completion: ", throwable);
        }
    }
}
