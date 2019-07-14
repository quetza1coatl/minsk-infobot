package com.quetzalcoatl.telebot.util;

public enum InfoType {

    MOVIE("movie", "киноафиша"),
    WEATHER_FORECAST("weather forecast", "прогноз погоды"),
    EXCHANGE_RATES("exchange rates", "курсы валют"),
    NEWS("news", "новости");

    public final String value;
    public final String displayName;

    InfoType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

}
