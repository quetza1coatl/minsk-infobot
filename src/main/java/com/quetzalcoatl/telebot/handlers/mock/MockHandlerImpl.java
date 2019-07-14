package com.quetzalcoatl.telebot.handlers.mock;

import com.quetzalcoatl.telebot.handlers.Handler;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MockHandlerImpl implements Handler {

    @Override
    public boolean isSuitable(String text) {
        return text.equalsIgnoreCase("привет");
    }

    @Override
    public String getText(Update update) {
        return "hello, guys!!!";
    }

}
