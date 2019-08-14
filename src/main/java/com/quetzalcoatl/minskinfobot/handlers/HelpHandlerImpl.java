package com.quetzalcoatl.minskinfobot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

public class HelpHandlerImpl implements Handler {
    private static final String WIND = "\uD83C\uDF2C";
    private static final String HUMIDITY = "\uD83D\uDCA7";
    private static final String PRESSURE = "\u21D3";
    private static final String CLOUDINESS = "\u2601";

    @Override
    public final List<String> getText(Update update) {
        String generalMessage = "*Список доступных функций:*\n\n";
        String weatherMessage = "*--Прогноз погоды--*\n" +
                "Погода предоставляется для города Минска с интервалом в три часа (10 записей). [Источник.](https://openweathermap.org/)" +
                "\nРасшифровка условных обозначений:\n" +
                WIND + " - скорость ветра в метрах в секунду;\n" +
                PRESSURE + " - давление над уровнем моря в гектопаскалях;\n" +
                CLOUDINESS + " - облачность в процентах;\n" +
                HUMIDITY + " - влажность в процентах.\n\n";
        String newsMessage = "*--Лента новостей--*\n" +
                "Список новостей с сайта [tut.by](https://news.tut.by/). Предоставляется в виде заголовка (гиперссылка)" +
                " и краткого описания новости. Нажав на заголовок новости, ты попадёшь на соответствующую страницу новостного сайта.\n\n";
        String movieMessage = "*--Киноафиша Минска--*\n" +
                "Список фильмов в кинотеатрах Минска с сайта [tut.by](https://afisha.tut.by/film/).\n" +
                "Названия фильмов кликабельны, ссылаются на соответствующую страницу с описанием фильма и доступными киносеансами.\n\n";
        String rateExchangeMessage = "*--Курсы валют--*\n" +
                "Краткая информация по курсам валют [Национального Банка Республики Беларусь](http://www.nbrb.by/) (USD-BYN, EUR-BYN, UAH-BYN, RUB-BYN).";
        List<String> resultList = new ArrayList<>();
        // This is a very short message, that will never exceed the size limit of message. So it hasn't to split.
        resultList.add( generalMessage + weatherMessage + newsMessage + movieMessage + rateExchangeMessage);
        return resultList;
    }

}
