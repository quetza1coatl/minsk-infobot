package com.quetzalcoatl.telebot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Handler {

    /**
     * @return boolean, который означает, может ли данный обработчик обработать текущий запрос.
     * Анализ входящего текста осуществляется с помощью, например, регулярных выражений
     */
    boolean isSuitable(String text);

    /**@return answer from the parser (or generator) or null if it can't do this
     */

    String getText(Update update);
}
