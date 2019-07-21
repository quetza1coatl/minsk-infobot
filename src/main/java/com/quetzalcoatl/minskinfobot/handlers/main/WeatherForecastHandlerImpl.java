package com.quetzalcoatl.minskinfobot.handlers.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quetzalcoatl.minskinfobot.handlers.Handler;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class WeatherForecastHandlerImpl implements Handler {

    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast?q=Minsk&units=metric&" +
            "APPID=" + System.getenv("OpenWeatherToken");
    private static final int NUMBER_OF_WEATHER_RECORDS = 10;
    private static final String INFO = "Погода для Минска с интервалом в 3 часа\n-----\n";

    private static Map<String, String> emojiMap;
    private static final Logger log = getLogger(WeatherForecastHandlerImpl.class);

    static {
        fillEmojiMap();
    }

    @Override
    public final String getText(Update update) {
        String result = null;
        URL url;
        ObjectMapper mapper = new ObjectMapper();

        try {
            url = new URL(WEATHER_URL);

            //todo если получаемый json это отказ API от предоставления данных (лимит запросов в минуту) -> return null
            JsonNode head = mapper.readTree(url);
            JsonNode mainArray = head.get("list");
            List<Weather> weatherList = new ArrayList<>();

            if (mainArray.isArray()) {
                for (final JsonNode line : mainArray) {
                    Weather weather = new Weather(
                            ZonedDateTime.ofInstant(Instant.ofEpochSecond(line.findValue("dt").asLong()), ZoneId.of("GMT+03:00")),
                            (int) Math.round(line.findValue("temp").asDouble()),
                            line.findValue("humidity").asInt(),
                            (int) Math.round(line.findValue("pressure").asDouble()),
                            line.get("weather").findValue("main").toString().replace("\"", ""),
                            line.findValue("description").toString().replace("\"", ""),
                            line.findValue("all").asInt(),
                            (int) Math.round(line.findValue("speed").asDouble())
                    );
                    weatherList.add(weather);
                }
            }
            // filtering time to avoid redundant information and collect result to string
            result = weatherList.stream()
                    .limit(NUMBER_OF_WEATHER_RECORDS)
                    .map(Weather::getFormattedText)
                    .collect(Collectors.joining("\n"));
        } catch (MalformedURLException e) {
            log.error("Can't get weather URL", e);
        } catch (IOException e) {
            log.error("Can't parse weather data", e);
        }
        return INFO + result;
    }


    private static class Weather {

        private static final String CELSIUS = "\u2103";
        private static final String WIND = "\uD83C\uDF2C";
        private static final String HUMIDITY = "\uD83D\uDCA6";
        private static final String CLOCK = "\uD83D\uDD50";
        private static final String PRESSURE = "\u21D3";
        private static final String CLOUDINESS = "\u2601";

        /**
         * Date time with a time-zone (UTC+3 for Minsk)
         */
        private ZonedDateTime dateTime;
        /**
         * Temperature, Celsius.
         */
        private int temperature;
        /**
         * Humidity, %
         */
        private int humidity;
        /**
         * Atmospheric pressure on the sea level, hPa
         */
        private int pressure;
        /**
         * General description
         */
        private String general;
        /**
         * Weather condition
         */
        private String description;
        /**
         * Cloudiness, %
         */
        private int cloudiness;
        /**
         * Wind speed, meter/sec
         */
        private int windSpeed;


        private Weather(
                ZonedDateTime dateTime, int temperature, int humidity, int pressure, String general,
                String description, int cloudiness, int windSpeed
        ) {
            this.dateTime = dateTime;
            this.temperature = temperature;
            this.humidity = humidity;
            this.pressure = pressure;
            this.general = general;
            this.description = description;
            this.cloudiness = cloudiness;
            this.windSpeed = windSpeed;
        }


        private String getFormattedText() {

            // format date - time
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedDateTime = "*" + dateTime.format(dateFormatter) +
                    "*  " + CLOCK + " " + dateTime.format(timeFormatter);

            // get weather emoji
            String weatherEmoji = emojiMap.getOrDefault(general, "");

            // format temperature
            String formattedTemperature = "*" + temperature + CELSIUS + "*";
            if (temperature > 0) {
                formattedTemperature = "+" + formattedTemperature;
            }

            //  cloudiness
            String formattedCloudiness = CLOUDINESS + cloudiness + "%";

            // pressure
            String formattedPressure = PRESSURE + pressure + " гПа";

            // humidity
            String formattedHumidity = HUMIDITY + humidity + "%";

            // wind
            String formattedWind = WIND + windSpeed + " м/сек";

            return String.format(
                    "%s\n%s  %s %s\n%s  %s  %s  %s\n",
                    formattedDateTime, weatherEmoji, formattedTemperature,
                    description, formattedWind, formattedPressure, formattedCloudiness, formattedHumidity);
        }

    }

    private static void fillEmojiMap() {
        emojiMap = new HashMap<String, String>() {{
            put("Clear", "\uD83C\uDF1E");
            put("Clouds", "\u2601");
            put("Rain", "\u2614");
            put("Drizzle", "\u2614");
            put("Thunderstorm", "\u26C8");
            put("Snow", "\uD83C\uDF28");
            put("Mist", "\uD83C\uDF2B");
            put("Smoke", "\uD83C\uDF2B");
            put("Dust", "\uD83C\uDF2B");
            put("Haze", "\uD83C\uDF2B");
            put("Fog", "\uD83C\uDF2B");
        }};
    }

}
