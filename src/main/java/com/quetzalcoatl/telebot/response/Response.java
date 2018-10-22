package com.quetzalcoatl.telebot.response;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Response {

    boolean isSuitable(String text);

    /**@return text bot response or null if it can't create response
     */

    String getText(Update update);
}
