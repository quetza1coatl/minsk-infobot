package com.quetzalcoatl.minskinfobot.handlers.main;

import com.quetzalcoatl.minskinfobot.handlers.Handler;
import org.telegram.telegrambots.meta.api.objects.Update;

//TODO STUB
public class NewsHandlerImpl implements Handler {

    /**
     * Command starting with '/' symbol and can be registered by BotFarther in list of commands
     */
    private static final String COMMAND_MESSAGE ="/news";

    @Override
    public boolean isSuitable(String text) {
        return text.equals(COMMAND_MESSAGE);
    }

    @Override
    public String getText(Update update) {
        return "STUB ^^ Новости наиприятнейшие, с какой начать?";
    }

}
