package gg.solarmc.kitpvp;

import gg.solarmc.kitpvp.kill.levelling.LevelConfig;
import gg.solarmc.kitpvp.messaging.MessageConfig;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.helper.ConfigurationHelper;

import java.io.IOException;

//i mean the only way i can provide code guaruntees to the configs would be using optionals, and
//i don't care enough about optionals to stick them here. We'll just have to guaruntee that these are non null via always calling load.
public class Configs {

    private final ConfigurationHelper<KitpvpConfig> kitpvpHelper;
    private final ConfigurationHelper<MessageConfig> messageHelper;
    private final ConfigurationHelper<LevelConfig> levelHelper;

    private KitpvpConfig config;
    private MessageConfig messageConfig;
    private LevelConfig levelConfig;

    public Configs(ConfigurationHelper<KitpvpConfig> kitpvpHelper, ConfigurationHelper<MessageConfig> messageHelper, ConfigurationHelper<LevelConfig> levelHelper) {
        this.kitpvpHelper = kitpvpHelper;
        this.messageHelper = messageHelper;
        this.levelHelper = levelHelper;
    }

    public void load() {

        try {
            this.config = kitpvpHelper.reloadConfigData();
            this.messageConfig = messageHelper.reloadConfigData();
            this.levelHelper.reloadConfigData();
        } catch (IOException | InvalidConfigException e) {
            throw new IllegalStateException(e);
        }

    }

    public KitpvpConfig getConfig() {
        return config;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

}
