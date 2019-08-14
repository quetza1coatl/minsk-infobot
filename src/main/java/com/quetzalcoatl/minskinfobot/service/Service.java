package com.quetzalcoatl.minskinfobot.service;

import com.quetzalcoatl.minskinfobot.MinskInfoBot;
import com.quetzalcoatl.minskinfobot.handlers.*;
import com.quetzalcoatl.minskinfobot.handlers.main.*;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import static org.slf4j.LoggerFactory.getLogger;

public class Service {

    private static final String SERVICE_ERROR_MESSAGE = "временно недоступен, попробуйте позже";
    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String EXCHANGE_RATES = "курсы валют";
    private static final String WEATHER_FORECAST = "прогноз погоды";
    private static final String MOVIE = "киноафиша";
    private static final String NEWS = "новости";
    private static final String CACHE_KEY = "Cache key";

    // Caching
    private URL cacheConfigUrl = getClass().getResource("/ehcache.xml");
    private XmlConfiguration xmlConfig = new XmlConfiguration(cacheConfigUrl);
    private CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);

    private static final Logger log = getLogger(Service.class);
    private final MinskInfoBot minskInfoBot;

    public Service(MinskInfoBot bot) {
        minskInfoBot = bot;
        cacheManager.init();
    }

    @SuppressWarnings("unchecked")
    public final void handleTextMessage(Update update) {
        List<String> response;
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
                break;
        }

        if ("none".equals(handler.getAlias())) {
            response = handler.getText(update);
            // Caching responses from handlers and getting them from cache
        } else {
            Cache<String, List> cache = cacheManager.getCache(handler.getAlias(), String.class, List.class);
            if (cache.containsKey(CACHE_KEY)) {
                response = cache.get(CACHE_KEY);
            } else {
                response = handler.getText(update);
                if (response != null) {
                    cache.put(CACHE_KEY, response);
                }
            }
        }


        if (response == null) {
            log.error("Unable to get data from {}", handler.getClass().getSimpleName());
            sendMsg(handler.getHandlerName() + " " + SERVICE_ERROR_MESSAGE, chatId, handler);

        } else {
            response.forEach(it -> sendMsg(it, chatId, handler));
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

    private void sendMsg(String answer, long chatID, Handler handler) {
        SendMessage message = new SendMessage();
        message.setChatId(chatID);
        message.setText(answer);
        getReplyKeyboardMarkup(message);
        //settings
        message.enableMarkdown(true);
        message.disableWebPagePreview();

        try {
            minskInfoBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("Sending message from {} failed", handler.getHandlerName(), e);
            sendMsg(handler.getHandlerName() + " " + SERVICE_ERROR_MESSAGE, chatID, handler);
        }
    }

}
