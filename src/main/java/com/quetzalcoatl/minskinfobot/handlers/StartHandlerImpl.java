package com.quetzalcoatl.minskinfobot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class StartHandlerImpl implements Handler {
    private static final String HAND = "\uD83D\uDD90";

    @Override
    public final List<String> getText(Update update) {
        String username = update.getMessage().getFrom().getFirstName();
        username = username != null ? username : "unknown person";

        List<String> resultList = new ArrayList<>();
        // This is a very short message, that will never exceed the size limit of message. So it hasn't to split.
        resultList.add(String.format("Привет, %s! " + HAND + "\n" +
                "Бот предоставляет информацию о курсах валют, прогнозе погоды, киносеансах и новостях." +
                " Информация актуальна для города Минска (и в ряде случаев для всей Беларуси)." +
                " Основные сервисы доступны через встроенную клавиатуру.\n\n" +
                "/help - описание доступных сервисов\n", username));
        return resultList;
    }

}
