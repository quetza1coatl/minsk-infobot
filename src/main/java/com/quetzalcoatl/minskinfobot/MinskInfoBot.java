package com.quetzalcoatl.minskinfobot;

import com.quetzalcoatl.minskinfobot.service.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.quetzalcoatl.minskinfobot.util.MainUtil.getProperties;

/**
 * Поставляет имя и токен бота.
 * Слушает запросы пользователя и в зависимости от их типа маппит их на соответствующий метод сервиса.
 * Инициализирует сервис, передавая в него экземпляр контроллера для возможности отправки сообщений из сервиса
 */

public class MinskInfoBot extends TelegramLongPollingBot {

    private Service service = new Service(this);


    @Override
    public String getBotUsername() {
        return getProperties().getProperty("BotUserName");
    }

    @Override
    public String getBotToken() {
        return getProperties().getProperty("BotToken");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            service.handleTextMessage(update);
        }
        //TODO: else ... not supported?
    }

}
