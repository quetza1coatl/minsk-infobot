package com.quetzalcoatl.minskinfobot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpHandlerImpl implements Handler {

    @Override
    public final String getText(Update update) {
        //TODO для чего создан, доступные функции
        String message = "help menu";
        return message;
    }

}
