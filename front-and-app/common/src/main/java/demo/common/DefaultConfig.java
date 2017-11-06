package demo.common;

import java.io.IOException;
import java.util.Properties;

public class DefaultConfig implements Config {

    private static final DefaultConfig CONF = new DefaultConfig();

    private Properties prop = new Properties();

    public static DefaultConfig getConfig() {
        return CONF;
    }

    private DefaultConfig() {
    }

    @Override
    public void load() {
        try {
            prop.load(DefaultConfig.class.getResourceAsStream("/app.properties"));
        } catch (IOException e) {
            //
        }
    }

    @Override
    public String getStrByKey(ConfigEntry key) {
        return prop.getProperty(key.name());
    }

    @Override
    public int getIntByKey(ConfigEntry key) {
        return Integer.parseInt(prop.getProperty(key.name()));
    }
}
