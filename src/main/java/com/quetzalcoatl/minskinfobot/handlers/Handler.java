package com.quetzalcoatl.minskinfobot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Handler {

    /**
     * @return answer from the parser (or generator) or null if it can't do this
     */
    String getText(Update update);

}
