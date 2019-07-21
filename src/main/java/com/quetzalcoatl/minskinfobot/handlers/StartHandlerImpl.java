package com.quetzalcoatl.minskinfobot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public class StartHandlerImpl implements Handler {
    private static final String HAND = "\uD83D\uDD90";

    @Override
    public final String getText(Update update) {
        String username = update.getMessage().getFrom().getFirstName();
        username = username != null ? username : "unknown person";
        return String.format("Привет, %s! " + HAND + "\n" +
                "Бот предоставляет информацию о курсах валют, прогнозе погоды, киносеансах, новостях." +
                " Информация актуальна для города Минска (и в ряде случаев для всей республики)." +
                " Основные сервисы доступны через встроенную клавиатуру.\n\n" +
                "/help - описание доступных сервисов\n", username);
    }

}
