package com.quetzalcoatl.telebot.service;

import com.quetzalcoatl.telebot.contoller.Controller;
import com.quetzalcoatl.telebot.handlers.Handler;
import com.quetzalcoatl.telebot.util.Constants;
import com.quetzalcoatl.telebot.util.MainUtil;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;


import static org.slf4j.LoggerFactory.getLogger;
import static java.lang.Math.toIntExact;

public class Service {
    private List<Handler> handlerList = MainUtil.getHandlerList();
    private static final Logger log = getLogger(Service.class);
    private final Controller controller;

    public Service(Controller controller) {
        this.controller = controller;
    }

    //TODO: оптимизировать логику if-else (иная последовательность, что-то еще...)
    public void handleTextMessage(Update update) {
        String response = null;
        String userRequest = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        //находим хэндлер.
        Handler handler = findHandler(userRequest);
        //Нету? Вызываем подсказку в виде инлайн-клавы.
        if (handler == null) {
            SendMessage message = getInlineKeyboardMessage(update.getMessage().getChatId());
            sendMsg(message);

            //Есть хэндлер? двигаем дальше
        } else {
            response = handler.getText(update);
            if (response == null) {
                log.error("Unable to get data from {}", handler.getClass().getSimpleName());
                sendMsg(Constants.SERVICE_ERROR_MESSAGE, chatId);

            } else {
                sendMsg(response, chatId);
            }
        }
    }


    public void handleCallbackQuery(Update update) {
        String response = null;
        String callbackData = update.getCallbackQuery().getData();
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long callbackChatId = update.getCallbackQuery().getMessage().getChatId();
        EditMessageText editMessageText = new EditMessageText()
                .setChatId(callbackChatId)
                .setMessageId(toIntExact(messageId));
        // TODO: в данной точке, если не найден хэндлер (как такое возможно???)  -> служ.сообщение
        Handler handler = findHandler(callbackData);
        if (handler == null) {
            log.error("Unable find handler to callbackQuery: {}", callbackData);
            editMessageText.setText(Constants.SERVICE_ERROR_MESSAGE);

        } else {

            response = handler.getText(update);
            if (response == null) {
                log.error("Unable to get data from {}", handler.getClass().getSimpleName());
                editMessageText.setText(Constants.SERVICE_ERROR_MESSAGE);

            } else {
                editMessageText.setText(response);
            }
        }
        sendMsg(editMessageText);
    }


    private SendMessage getInlineKeyboardMessage(long chatID) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);

        message.setText(Constants.HINT_KEYS);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        //TODO: оптимизировать создание списков. immutable?
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        //создаем первый ряд, добавляем в него кнопку (всего пока один ряд)

        rowInline1.add(new InlineKeyboardButton().setText(Constants.INLINE_TEXT_WEATHER_FORECAST).setCallbackData(Constants.CALLBACK_DATA_WEATHER_FORECAST));
        rowInline1.add(new InlineKeyboardButton().setText(Constants.INLINE_TEXT_MOVIE).setCallbackData(Constants.CALLBACK_DATA_MOVIE));
        //добавляем второй ряд
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(new InlineKeyboardButton().setText(Constants.INLINE_TEXT_EXCHANGE_RATES).setCallbackData(Constants.CALLBACK_DATA_EXCHANGE_RATES));
        rowInline2.add(new InlineKeyboardButton().setText(Constants.INLINE_TEXT_NEWS).setCallbackData(Constants.CALLBACK_DATA_NEWS));
        //добавляем наш ряд в массив рядов
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);
        // Add it to the message
        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);
        return message;
    }

    private Handler findHandler(String userRequest) {
        return handlerList.stream()
                .filter(r -> r.isSuitable(userRequest))
                .findFirst()
                .orElse(null);

    }

        private void sendMsg(String answer, long chatID) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(answer);
        sendMsg(message);
    }

    private void sendMsg(SendMessage message) {
        try {
            controller.execute(message);
        } catch (TelegramApiException e) {
            log.error("Sending message failed", e);
        }

    }

    private void sendMsg(EditMessageText message) {
        try {
            controller.execute(message);
        } catch (TelegramApiException e) {
            log.error("Sending message failed", e);
        }

    }


}
