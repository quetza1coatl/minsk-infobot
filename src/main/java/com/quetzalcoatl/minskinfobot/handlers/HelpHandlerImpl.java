package com.quetzalcoatl.minskinfobot.handlers;

import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpHandlerImpl implements Handler {
    private static final String WIND = "\uD83C\uDF2C";
    private static final String HUMIDITY = "\uD83D\uDCA6";
    private static final String PRESSURE = "\u21D3";
    private static final String CLOUDINESS = "\u2601";

    @Override
    public final String getText(Update update) {
        //TODO для чего создан, доступные функции
        String message = "*Список доступных функций:*\n\n" +
                "*--Прогноз погоды--*\n" +
                "Погода предоставляется для города Минска с интервалом в три часа (10 записей). Расшифровка условных обозначений:\n" +
                WIND + " - скорость ветра в метрах в секунду;\n" +
                PRESSURE + " - давление над уровнем моря в гектопаскалях;\n" +
                CLOUDINESS + " - облачность в процентах;\n" +
                HUMIDITY + " - влажность в процентах.\n";
        return message;
    }

}
