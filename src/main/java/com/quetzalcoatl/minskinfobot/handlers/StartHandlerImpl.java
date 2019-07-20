package com.quetzalcoatl.minskinfobot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public class StartHandlerImpl implements Handler {

    @Override
    public final String getText(Update update) {
        String username = update.getMessage().getFrom().getFirstName();
        username = username != null ? username : "unknown person";
        return String.format("Привет человекам!!! Я пришёл с миром :)\n" +
                "/help - описание доступных функций и способов взаимодействия\n"
                + "Да прибудет с тобой сила, %s!", username);
    }

}
