package com.quetzalcoatl.telebot.util;

import com.quetzalcoatl.telebot.handlers.*;
import com.quetzalcoatl.telebot.handlers.Handler;
import com.quetzalcoatl.telebot.handlers.main.ExchangeRateHandlerImpl;
import com.quetzalcoatl.telebot.handlers.main.MovieHandlerImpl;
import com.quetzalcoatl.telebot.handlers.main.NewsHandlerImpl;
import com.quetzalcoatl.telebot.handlers.main.WeatherForecastHandlerImpl;
import com.quetzalcoatl.telebot.handlers.mock.MockHandlerImpl;
import org.slf4j.Logger;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.slf4j.LoggerFactory.getLogger;

public final class MainUtil {
    private static final Logger log = getLogger(MainUtil.class);
    private MainUtil(){

    }

    public static Properties getProperties() {
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
//TODO где лучше хранить все имплементации??? В контроллере?
    public static List<Handler> getHandlerList(){
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
