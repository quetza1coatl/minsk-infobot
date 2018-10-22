package com.quetzalcoatl.telebot;

import com.quetzalcoatl.telebot.contoller.MainController;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;


public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new MainController());
            System.out.println("Bot has been started");
            //TODO log
        } catch (TelegramApiRequestException e) {
            //TODO log
            e.printStackTrace();

        }


    }



}
