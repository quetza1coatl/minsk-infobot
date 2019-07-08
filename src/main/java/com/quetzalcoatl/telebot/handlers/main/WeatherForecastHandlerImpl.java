package com.quetzalcoatl.telebot.handlers.main;

import com.quetzalcoatl.telebot.handlers.Handler;
import com.quetzalcoatl.telebot.util.InfoType;
import org.telegram.telegrambots.meta.api.objects.Update;

//TODO STUB
public class WeatherForecastHandlerImpl implements Handler {

    @Override
    public boolean isSuitable(String text) {
        return text.equals(InfoType.WEATHER_FORECAST.value);
    }

    @Override
    public String getText(Update update) {
        return "STUB ^^ Погода будет прекрасной";
    }
}
