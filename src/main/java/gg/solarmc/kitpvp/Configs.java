package gg.solarmc.kitpvp;

import gg.solarmc.kitpvp.messaging.MessageConfig;
import me.aurium.beetle.api.file.UncheckedIOException;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.helper.ConfigurationHelper;

import java.io.IOException;

//i mean the only way i can provide code guaruntees to the configs would be using optionals, and
//i don't care enough about optionals to stick them here. We'll just have to guaruntee that these are non null via always calling load.
public class Configs {

    private final ConfigurationHelper<KitpvpConfig> kitpvpHelper;
    private final ConfigurationHelper<MessageConfig> messageHelper;

    private KitpvpConfig config;
    private MessageConfig messageConfig;

    public Configs(ConfigurationHelper<KitpvpConfig> kitpvpHelper, ConfigurationHelper<MessageConfig> messageHelper) {
        this.kitpvpHelper = kitpvpHelper;
        this.messageHelper = messageHelper;
    }

    public void load() throws IOException, InvalidConfigException {
        this.config = kitpvpHelper.reloadConfigData();
        this.messageConfig = messageHelper.reloadConfigData();
    }

    public void loadTry() {
        try {
            load();
        } catch (IOException | InvalidConfigException e) {
            throw new UncheckedIOException(e);
        }
    }

    public KitpvpConfig getConfig() {
        return config;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

}
