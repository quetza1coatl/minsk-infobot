package com.quetzalcoatl.telebot.service;

import com.quetzalcoatl.telebot.contoller.Controller;
import com.quetzalcoatl.telebot.handlers.Handler;
import com.quetzalcoatl.telebot.util.InfoType;
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

    private static final String SERVICE_ERROR_MESSAGE = "Сервис временно недоступен, попробуйте позже";
    private static final String HINT_KEYS = "Вы можете выбрать доступный вариант "
            + "или продолжить общение с ботом ^_^";
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
        // fins handler
        Handler handler = findHandler(userRequest);
        // If can't find handler, create inline keyboard with available requests
        if (handler == null) {
            SendMessage message = getInlineKeyboardMessage(update.getMessage().getChatId());
            sendMsg(message);

            // If handler has been found truing to get response
        } else {
            response = handler.getText(update);
            if (response == null) {
                log.error("Unable to get data from {}", handler.getClass().getSimpleName());
                sendMsg(SERVICE_ERROR_MESSAGE, chatId);

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
            log.error("Unable to find handler to callbackQuery: {}", callbackData);
            editMessageText.setText(SERVICE_ERROR_MESSAGE);

        } else {
            response = handler.getText(update);
            if (response == null) {
                log.error("Unable to get data from {}", handler.getClass().getSimpleName());
                editMessageText.setText(SERVICE_ERROR_MESSAGE);

            } else {
                editMessageText.setText(response);
            }
        }
        sendMsg(editMessageText);
    }

    private SendMessage getInlineKeyboardMessage(long chatID) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(HINT_KEYS);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // create first row, add two buttons
        List<InlineKeyboardButton> rowInline1 = new ArrayList<>();
        rowInline1.add(new InlineKeyboardButton()
                .setText(InfoType.WEATHER_FORECAST.displayName)
                .setCallbackData(InfoType.WEATHER_FORECAST.value));
        rowInline1.add(new InlineKeyboardButton()
                .setText(InfoType.MOVIE.displayName)
                .setCallbackData(InfoType.MOVIE.value));

        // create second row, add buttons
        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(new InlineKeyboardButton()
                .setText(InfoType.EXCHANGE_RATES.displayName)
                .setCallbackData(InfoType.EXCHANGE_RATES.value));
        rowInline2.add(new InlineKeyboardButton()
                .setText(InfoType.NEWS.displayName)
                .setCallbackData(InfoType.NEWS.value));

        // add rows to array
        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);

        // Add array of rows to the message
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
