package com.quetzalcoatl.telebot.handlers.main;

import com.quetzalcoatl.telebot.handlers.Handler;
import com.quetzalcoatl.telebot.util.Constants;
import org.telegram.telegrambots.meta.api.objects.Update;

//TODO STUB
public class MovieHandlerImpl implements Handler {
    @Override
    public boolean isSuitable(String text) {
        return text.equals(Constants.CALLBACK_DATA_MOVIE);
    }

    @Override
    public String getText(Update update) {
        return "Да ничё интересного пока не идёт(";
    }
}
