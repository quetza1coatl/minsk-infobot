package com.quetzalcoatl.telebot.handlers.main;

import com.quetzalcoatl.telebot.handlers.Handler;
import com.quetzalcoatl.telebot.util.Constants;
import org.telegram.telegrambots.meta.api.objects.Update;

//TODO STUB
public class WeatherForecastHandlerImpl implements Handler {

    @Override
    public boolean isSuitable(String text) {
        return text.equals(Constants.CALLBACK_DATA_WEATHER_FORECAST);
    }

    @Override
    public String getText(Update update) {
        return "Погода будет прекрасной";
    }
}
