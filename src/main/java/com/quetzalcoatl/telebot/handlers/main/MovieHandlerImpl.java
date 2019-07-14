package com.quetzalcoatl.telebot.handlers.main;

import com.quetzalcoatl.telebot.handlers.Handler;
import org.telegram.telegrambots.meta.api.objects.Update;

//TODO STUB
public class MovieHandlerImpl implements Handler {

    /**
     * Command starting with '/' symbol and can be registered by BotFarther in list of commands
     */
    private static final String COMMAND_MESSAGE ="/movie";

    @Override
    public boolean isSuitable(String text) {
        return text.equals(COMMAND_MESSAGE);
    }

    @Override
    public String getText(Update update) {
        return "STUB ^^ Да ничё интересного пока не идёт(";
    }

}
