package com.quetzalcoatl.telebot.response;

import org.telegram.telegrambots.meta.api.objects.Update;

public class StartResponseImpl implements Response {
    @Override
    public boolean isSuitable(String text) {
        return (text.equals("/start") || text.equalsIgnoreCase("старт"));
    }

    //TODO: сразу лепить ему инлайн-клавиатуру???
    @Override
    public String getText(Update update) {
        String username = update.getMessage().getFrom().getFirstName();
        String message = String.format("Привет человекам!!! Я пришёл с миром :)\n"
                //TODO для чего создан, доступные функции (см. инлайн клава), распознавание речи
                               + "Да прибудет с тобой сила, %s!", username);
        return message;
    }
}
