package com.quetzalcoatl.minskinfobot.service;

import com.quetzalcoatl.minskinfobot.MinskInfoBot;
import com.quetzalcoatl.minskinfobot.handlers.*;
import com.quetzalcoatl.minskinfobot.handlers.main.*;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;


import static org.slf4j.LoggerFactory.getLogger;

public class Service {

    private static final String SERVICE_ERROR_MESSAGE = "Сервис временно недоступен, попробуйте позже";
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String EXCHANGE_RATES = "курсы валют";
    private static final String WEATHER_FORECAST = "прогноз погоды";
    private static final String MOVIE = "киноафиша";
    private static final String NEWS = "новости";


    private static final Logger log = getLogger(Service.class);
    private final MinskInfoBot minskInfoBot;

    public Service(MinskInfoBot bot) {
        minskInfoBot = bot;
    }


    public final void handleTextMessage(Update update) {
        String response;
        String userRequest = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        Handler handler;

        switch (userRequest) {
            case (START):
                handler = new StartHandlerImpl();
                break;
            case (HELP):
                handler = new HelpHandlerImpl();
                break;
            case (EXCHANGE_RATES):
                handler = new ExchangeRateHandlerImpl();
                break;
            case (WEATHER_FORECAST):
                handler = new WeatherForecastHandlerImpl();
                break;
            case (MOVIE):
                handler = new MovieHandlerImpl();
                break;
            case (NEWS):
                handler = new NewsHandlerImpl();
                break;
            default:
                handler = new HelpHandlerImpl();
        }
        response = handler.getText(update);
        if (response == null) {
            log.error("Unable to get data from {}", handler.getClass().getSimpleName());
            sendMsg(SERVICE_ERROR_MESSAGE, chatId);

        } else {
            sendMsg(response, chatId);
        }
    }

    private void getReplyKeyboardMarkup(SendMessage message) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        message.setReplyMarkup(keyboard);

        // apply settings
        keyboard.setSelective(true);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> rowList = new ArrayList<>();

        // create first row, add two buttons
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add(new KeyboardButton(EXCHANGE_RATES));
        firstRow.add(new KeyboardButton(WEATHER_FORECAST));

        // create second row, add two buttons
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(new KeyboardButton(MOVIE));
        secondRow.add(new KeyboardButton(NEWS));

        rowList.add(firstRow);
        rowList.add(secondRow);

        keyboard.setKeyboard(rowList);
    }

    private void sendMsg(String answer, long chatID) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(answer);
        getReplyKeyboardMarkup(message);
        message.enableMarkdown(true);
        message.disableWebPagePreview();
        try {
            minskInfoBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Sending message failed", e);
        }
    }

}
