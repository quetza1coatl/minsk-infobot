package com.quetzalcoatl.telebot;

import com.quetzalcoatl.telebot.util.InfoType;

import java.util.*;

public class HandlersTestData {

    private HandlersTestData(){ }

    public static final String TEST_MESSAGE_EXCHANGE_RATE_KEY = "exchangeRate";
    public static final String TEST_MESSAGE_START_KEY = "/start";

        private final static Map<String, List<String>> TEST_MESSAGES = new HashMap<>();


    static {
        List<String> exchangeRateHandlerMessages = Arrays.asList(InfoType.EXCHANGE_RATES.value, "/exchange",
                "Что с курсом валют","Эй, что с курсом валют?", "дай курс валюты",
                "курс валюты", "курс валюты!", "курсы валют", "курсы валют?", "Какие на сегодня курсы валюты");
        List<String> startHandlerMessages = Arrays.asList("/start");

        TEST_MESSAGES.put(TEST_MESSAGE_EXCHANGE_RATE_KEY, exchangeRateHandlerMessages);
        TEST_MESSAGES.put(TEST_MESSAGE_START_KEY, startHandlerMessages);
    }

    public static List<String>  getTargetMessages(String key) throws Exception {
        checkKey(key);
        return TEST_MESSAGES.get(key);
    }

    public static Map<String, List<String>> getAnotherMessages(String key) throws Exception {
        checkKey(key);
        Map<String, List<String>> map = new HashMap<>(TEST_MESSAGES);
        map.remove(key);
        return map;
    }

    private static void checkKey(String key) throws Exception {
        if(key == null || key.isEmpty()){
            throw new Exception("Key is null or empty");
        }
        if(!TEST_MESSAGES.containsKey(key)){
            throw new Exception("TEST_MESSAGES doesn't contain this key");
        }
    }

}
