package com.quetzalcoatl.minskinfobot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpHandlerImpl implements Handler {

    @Override
    public boolean isSuitable(String text) {
        return text.equals("/help");
    }

    @Override
    public String getText(Update update) {
        //TODO для чего создан, доступные функции, распознавание речи
        String message = "help_menu";
        return message;
    }

}
