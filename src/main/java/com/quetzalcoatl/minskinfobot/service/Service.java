package com.quetzalcoatl.minskinfobot.service;

import com.quetzalcoatl.minskinfobot.MinskInfoBot;
import com.quetzalcoatl.minskinfobot.handlers.Handler;
import com.quetzalcoatl.minskinfobot.handlers.HelpHandlerImpl;
import com.quetzalcoatl.minskinfobot.handlers.StartHandlerImpl;
import com.quetzalcoatl.minskinfobot.handlers.main.ExchangeRateHandlerImpl;
import com.quetzalcoatl.minskinfobot.handlers.main.MovieHandlerImpl;
import com.quetzalcoatl.minskinfobot.handlers.main.NewsHandlerImpl;
import com.quetzalcoatl.minskinfobot.handlers.main.WeatherForecastHandlerImpl;
import com.quetzalcoatl.minskinfobot.util.InfoType;
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
    private static final Logger log = getLogger(Service.class);
    private final MinskInfoBot minskInfoBot;

    public Service(MinskInfoBot minskInfoBot) {
        this.minskInfoBot = minskInfoBot;
    }


    public void handleTextMessage(Update update) {
        String response;
        String userRequest = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        Handler handler;
        //TODO try to use Enum!!!
        switch (userRequest) {
            case ("/start"):
                handler = new StartHandlerImpl();
                break;
            case ("/help"):
                handler = new HelpHandlerImpl();
                break;
            case ("курсы валют"):
                handler = new ExchangeRateHandlerImpl();
                break;
            case ("прогноз погоды"):
                handler = new WeatherForecastHandlerImpl();
                break;
            case ("киноафиша"):
                handler = new MovieHandlerImpl();
                break;
            case ("новости"):
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
        firstRow.add(new KeyboardButton(InfoType.EXCHANGE_RATES.displayName));
        firstRow.add(new KeyboardButton(InfoType.WEATHER_FORECAST.displayName));

        // create second row, add two buttons
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add(new KeyboardButton(InfoType.MOVIE.displayName));
        secondRow.add(new KeyboardButton(InfoType.NEWS.displayName));

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
        try {
            minskInfoBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Sending message failed", e);
        }
    }

}
