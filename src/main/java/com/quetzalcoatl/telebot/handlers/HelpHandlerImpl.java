package com.quetzalcoatl.telebot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpHandlerImpl implements Handler {
    @Override
    public boolean isSuitable(String text) {
        return (text.equals("/help") || text.equalsIgnoreCase("помощь"));
    }

    @Override
    public String getText(Update update) {
        //TODO для чего создан, доступные функции, распознавание речи
        String message = "help_menu";
        return message;
    }
}
