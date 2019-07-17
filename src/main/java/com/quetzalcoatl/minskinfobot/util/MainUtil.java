package com.quetzalcoatl.minskinfobot.util;

import org.slf4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

public final class MainUtil {

    private static final Logger log = getLogger(MainUtil.class);

    private MainUtil() {}


    public static Properties getProperties() {
        //TODO Config Vars for Heroku -> >${name, token}
        Properties properties = new Properties();
        InputStream is = MainUtil.class.getClassLoader().getResourceAsStream("botCredentials.properties");
        if (is != null) {
            try {
                properties.load(is);
            } catch (IOException e) {
                log.error("Loading 'botCredentials.properties' failed", e);
            }
        }
        return properties;
    }

}
