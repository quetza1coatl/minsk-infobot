package com.quetzalcoatl.telebot.contoller;

import com.quetzalcoatl.telebot.service.Service;
import com.quetzalcoatl.telebot.service.ServiceImpl;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.quetzalcoatl.telebot.util.MainUtil.getProperties;

public class MainController extends TelegramLongPollingBot {
    private Service service = new ServiceImpl();

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
        //TODO: более похожее название методов. Что делают, что возвращают.
        String answer = null;
        if (update.hasMessage())
            answer = service.getTextResponse(update);
        else if (update.hasCallbackQuery())
            service.handleCallbackQuery(update);

        //TODO: обработка на null. В этом случае вызывать подсказку.
        if (answer != null)
            sendMsg(answer, update.getMessage().getChatId());


    }


    private void sendMsg(String answer, long chatID) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(answer);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


}
