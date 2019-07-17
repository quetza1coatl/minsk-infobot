package com.quetzalcoatl.minskinfobot.util;

import com.quetzalcoatl.minskinfobot.handlers.*;
import com.quetzalcoatl.minskinfobot.handlers.Handler;
import com.quetzalcoatl.minskinfobot.handlers.main.ExchangeRateHandlerImpl;
import com.quetzalcoatl.minskinfobot.handlers.main.MovieHandlerImpl;
import com.quetzalcoatl.minskinfobot.handlers.main.NewsHandlerImpl;
import com.quetzalcoatl.minskinfobot.handlers.main.WeatherForecastHandlerImpl;
import com.quetzalcoatl.minskinfobot.handlers.mock.MockHandlerImpl;
import org.slf4j.Logger;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
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

    //TODO где лучше хранить все имплементации??? В контроллере? см. паттерн Context?? Or pattern Factory

    /**
     * In case of adding new "/"-commands they SHOULD  be provided to the BotFather (/setcommands)
     * These commands are called COMMAND_MESSAGE in the appropriate handler.
     */
    public static List<Handler> getHandlerList() {
        return Arrays.asList(
                new StartHandlerImpl(),
                new HelpHandlerImpl(),
                new MockHandlerImpl(),
                new ExchangeRateHandlerImpl(),
                new MovieHandlerImpl(),
                new NewsHandlerImpl(),
                new WeatherForecastHandlerImpl()
        );
    }

}
