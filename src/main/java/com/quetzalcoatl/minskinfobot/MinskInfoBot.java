package com.quetzalcoatl.minskinfobot;

import com.quetzalcoatl.minskinfobot.service.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Поставляет имя и токен бота.
 * Инициализирует сервис, передавая в него экземпляр контроллера для возможности отправки сообщений из сервиса
 * Слушает запросы пользователя и передает в сервис.
 */

public class MinskInfoBot extends TelegramLongPollingBot {

    private Service service = new Service(this);

    @Override
    public final String getBotUsername() { return System.getenv("BotUserName"); }

    @Override
    public final String getBotToken() { return System.getenv("BotToken"); }

    @Override
    public final void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            service.handleTextMessage(update);
        }
        //TODO: else ... not supported?
    }

}
