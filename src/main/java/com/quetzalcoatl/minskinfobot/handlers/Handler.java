package com.quetzalcoatl.minskinfobot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.List;

public interface Handler {

    /**
     * Get alias for caching.
     */
    default String getAlias(){
        return "none";
    }

    default String getHandlerName(){
        return "Сервис";
    }

    /**
     * List is used instead of simple String because of the need to split the Bot answer into pieces when
     * a certain length limit is exceeded.
     * @return answer from the parser (or generator) or null if it can't do this.
     */
    List<String> getText(Update update);

}
