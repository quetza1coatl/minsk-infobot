package com.quetzalcoatl.telebot.response.mock;

import com.quetzalcoatl.telebot.response.Response;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MockResponseImpl implements Response {
    @Override
    public boolean isSuitable(String text) {
        return text.equalsIgnoreCase("привет");
    }

    @Override
    public String getText(Update update) {
        return "hello, guys!!!";
    }
}
