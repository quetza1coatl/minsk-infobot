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
    private static final LocalTime MORNING_TIME = LocalTime.of(9, 0);
    private static final LocalTime MIDDLE_TIME = LocalTime.of(15, 0);
    private static final LocalTime EVENING_TIME = LocalTime.of(21, 0);
    private static final String CELCIUS = "\u2103";
    private static Map<String, String> emojiMap;

    static {
        fillEmojiMap();
    }

    private static final Logger log = getLogger(WeatherForecastHandlerImpl.class);

    @Override
    public String getText(Update update) {
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
                    .filter(weather -> weather.getTime().equals(MORNING_TIME) ||
                            weather.getTime().equals(MIDDLE_TIME) ||
                            weather.getTime().equals(EVENING_TIME))
                    .map(Weather::toString)
                    .limit(10)
                    .collect(Collectors.joining("\n"));
        } catch (MalformedURLException e) {
            log.error("Can't get weather URL", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    private static class Weather {

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
                ZonedDateTime dateTime, int temperature, int humidity, String general,
                String description, int cloudiness, int windSpeed
        ) {
            this.dateTime = dateTime;
            this.temperature = temperature;
            this.humidity = humidity;
            this.general = general;
            this.description = description;
            this.cloudiness = cloudiness;
            this.windSpeed = windSpeed;
        }


        LocalTime getTime() {
            return dateTime.toLocalTime();
        }

        //TODO отформатировать вывод
        @Override
        public String toString() {
            // format date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM - HH:mm");
            String formattedDateTime = dateTime.format(formatter);
            // get weather emoji
            String weatherEmoji = emojiMap.getOrDefault(general, "");

            return String.format("%s\n%s ", formattedDateTime, weatherEmoji);
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
