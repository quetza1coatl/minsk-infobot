package com.quetzalcoatl.minskinfobot;

import com.quetzalcoatl.minskinfobot.service.Service;
import org.slf4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import static org.slf4j.LoggerFactory.getLogger;

public final class Main {

    private Main(){}


    private static final Logger log = getLogger(Service.class);

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new MinskInfoBot());
            log.info("Bot has been registered");
        } catch (TelegramApiRequestException e) {
            log.error("Bot registration failed", e);
        }
    }

}
