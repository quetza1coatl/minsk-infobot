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

/**
 * This parser based on free weather  <a href="https://openweathermap.org">API</a>.
 * Free API allows to do no more than 60 calls per minute.
 * The weather data in API system is updated no more than one time every 10 minutes.
 */
public class WeatherForecastHandlerImpl implements Handler {

    private static final String HANDLER_NAME = "Сервис прогноза погоды";
    private static final String ALIAS = "weather";
    private static final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/forecast?id=625144&units=metric&" +
            "APPID=" + System.getenv("OpenWeatherToken");
    private static final int NUMBER_OF_WEATHER_RECORDS = 10;
    private static final String INFO = "*Прогноз погоды (г. Минск)*\n\n";
    private static Map<String, String> emojiMap = new HashMap<>();
    private static Map<String, String> dictionary = new HashMap<>();
    private static final Logger log = getLogger(WeatherForecastHandlerImpl.class);

    static {
        fillEmojiMap();
        fillDictionary();
    }

    @Override
    public String getAlias() {
        return ALIAS;
    }

    @Override
    public String getHandlerName() {
        return HANDLER_NAME;
    }

    @Override
    public final List<String> getText(Update update) {
        String result = null;
        URL url;
        ObjectMapper mapper = new ObjectMapper();

        try {
            url = new URL(WEATHER_URL);
            JsonNode head = mapper.readTree(url);
            JsonNode mainArray = head.get("list");
            // If the number of requests per minute to API is exceeded, API returns a json response,
            // that cannot be processed with this logic. The value of mainArray in this case will be null.
            if (mainArray == null) {
                return null;
            }
            List<Weather> weatherList = new ArrayList<>();
            if (mainArray.isArray()) {
                for (final JsonNode line : mainArray) {
                    //  It is possible to meet more than one weather condition. The first weather condition in API
                    // respond is primary. Variable `weatherCondition` is the first condition.
                    JsonNode weatherCondition = line.get("weather").get(0);
                    // If json contains a zero value, the corresponding field of the object
                    // is filled with a Integer.MIN_VALUE, which is processed further in getFormattedText()
                    Weather weather = new Weather(
                            ZonedDateTime.ofInstant(Instant.ofEpochSecond(line.findValue("dt").asLong()), ZoneId.of("GMT+03:00")),
                            (int) Math.round(line.findValue("temp").asDouble(Integer.MIN_VALUE)),
                            line.findValue("humidity").asInt(Integer.MIN_VALUE),
                            (int) Math.round(line.findValue("pressure").asDouble(Integer.MIN_VALUE)),
                            weatherCondition.findValue("main").toString().replace("\"", "").trim(),
                            weatherCondition.findValue("description").toString().replace("\"", ""),
                            line.findValue("all").asInt(Integer.MIN_VALUE),
                            (int) Math.round(line.findValue("speed").asDouble(Integer.MIN_VALUE))
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

        List<String> resultList = new ArrayList<>();
        // This is a pretty short message, that will never exceed the size limit of message. So it hasn't to split.
        resultList.add(INFO + result);
        return resultList;
    }

    private static class Weather {

        private static final String CELSIUS = "\u2103";
        private static final String WIND = "\uD83C\uDF2C";
        private static final String HUMIDITY = "\uD83D\uDCA7";
        private static final String PRESSURE = "\u21D3";
        private static final String CLOUDINESS = "\u2601";
        private static final String N_A = "N/A";

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
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE").withLocale(Locale.forLanguageTag("RU"));
            String formattedDateTime = String.format("_%s %s (%s)_",
                    dateTime.format(timeFormatter), dateTime.format(dayFormatter), dateTime.format(dateFormatter));

            // get processed weather emoji
            String weatherEmoji = getMainWeatherEmoji();

            // format temperature. Null-check from Json
            String formattedTemperature =
                    temperature != Integer.MIN_VALUE ? "*" + temperature + " " + CELSIUS + "*" : "temp: "+ N_A;
            if (temperature > 0) {
                formattedTemperature = "+" + formattedTemperature;
            }

            // description. Null-check from Json. Getting data from dictionary
            String formattedDescription = "null".equals(description) ?
                    "description: " + N_A : dictionary.getOrDefault(description, description);

            //  cloudiness. Null-check from Json
            String formattedCloudiness =
                    cloudiness != Integer.MIN_VALUE ? CLOUDINESS + cloudiness + "%" : CLOUDINESS + N_A;

            // pressure. Null-check from Json
            String formattedPressure =
                    pressure != Integer.MIN_VALUE ? PRESSURE + pressure + " гПа" : PRESSURE + N_A;

            // humidity. Null-check from Json
            String formattedHumidity =
                    humidity != Integer.MIN_VALUE ? HUMIDITY + humidity + "%" : HUMIDITY + N_A;

            // wind. Null-check from Json
            String formattedWind =
                    windSpeed != Integer.MIN_VALUE ? WIND + windSpeed + " м/сек" : WIND + N_A;

            return String.format(
                    "    %s\n%s  %s %s\n%s  %s  %s  %s\n",
                    formattedDateTime, weatherEmoji, formattedTemperature,
                    formattedDescription, formattedPressure, formattedWind, formattedHumidity, formattedCloudiness);
        }

        private boolean isDay(LocalTime time) {
            return time.isAfter(LocalTime.of(5, 59)) && time.isBefore(LocalTime.of(21, 1));
        }

        private String getMainWeatherEmoji() {
            String result;

            switch (general) {
                case "Clear":
                    result = isDay(dateTime.toLocalTime()) ? "sun" : "moon";
                    break;
                case "Clouds":
                    if (!isDay(dateTime.toLocalTime())) {
                        result = "overcast clouds";
                    } else {
                        result = description;
                    }
                    break;
                case "Rain":
                    if (isDay(dateTime.toLocalTime()) && cloudiness <= 60) {
                        result = "custom: sun with rain";
                    } else {
                        result = general;
                    }
                    break;
                default:
                    result = general;
                    break;
            }

            return emojiMap.getOrDefault(result, "");
        }
    }

    // if key start with uppercase - this value used for API main type of weather and can be got by field Weather.general,
    // otherwise - a) value used for API subtype and can be got by field Weather.description,
    // b) it's a custom value (with prefix 'custom')
    private static void fillEmojiMap() {
        emojiMap.put("custom: sun with rain", "\uD83C\uDF26");
        emojiMap.put("sun", "\u2600");
        emojiMap.put("moon", "\uD83C\uDF1A");
        emojiMap.put("Clouds", "\u2601");
        emojiMap.put("few clouds", "\uD83C\uDF24");
        emojiMap.put("scattered clouds", "\uD83C\uDF25");
        emojiMap.put("broken clouds", "\uD83C\uDF25");
        emojiMap.put("overcast clouds", "\u2601");
        emojiMap.put("Rain", "\uD83C\uDF27");
        emojiMap.put("Drizzle", "\u2614");
        emojiMap.put("Thunderstorm", "\u26C8");
        emojiMap.put("Snow", "\uD83C\uDF28");
        emojiMap.put("Mist", "\uD83C\uDF2B");
        emojiMap.put("Smoke", "\uD83C\uDF2B");
        emojiMap.put("Dust", "\uD83C\uDF2B");
        emojiMap.put("Haze", "\uD83C\uDF2B");
        emojiMap.put("Fog", "\uD83C\uDF2B");
    }

    private static void fillDictionary() {
        // rain
        dictionary.put("light rain", "слабый дождь");
        dictionary.put("moderate rain", "умеренный дождь");
        dictionary.put("heavy intensity rain", "сильный дождь");
        dictionary.put("very heavy rain", "очень сильный дождь");
        dictionary.put("extreme rain", "ливень");
        dictionary.put("freezing rain", "ледяной дождь");
        dictionary.put("light intensity shower rain", "незначительный ливень");
        dictionary.put("shower rain", "ливень");
        dictionary.put("heavy intensity shower rain", "сильный ливень");
        dictionary.put("ragged shower rain", "местами ливни");
        // snow
        dictionary.put("light snow", "слабый снег");
        dictionary.put("Snow", "снег");
        dictionary.put("Heavy snow", "сильный снег");
        dictionary.put("Sleet", "мокрый снег");
        dictionary.put("Light shower sleet", "слабый мокрый снег");
        dictionary.put("Shower sleet", "сильный мокрый снег");
        dictionary.put("Rain and snow", "снег с дождем");
        dictionary.put("Light shower snow", "слабый снег с дождем");
        dictionary.put("Shower snow", "снегопад");
        dictionary.put("Heavy shower snow", "сильный снегопад");
        // cloudiness
        dictionary.put("clear sky", "безоблачно");
        dictionary.put("few clouds", "незначительная облачность");
        dictionary.put("scattered clouds", "средняя облачность");
        dictionary.put("broken clouds", "значительная облачность");
        dictionary.put("overcast clouds", "сплошная облачность");
        //atmosphere
        dictionary.put("mist", "туман");
        dictionary.put("Smoke", "дымка");
        dictionary.put("Haze", "дымка");
        dictionary.put("fog", "туман");
        dictionary.put("squalls", "шквал");
        // drizzle
        dictionary.put("light intensity drizzle", "слабая морось");
        dictionary.put("drizzle", "морось");
        dictionary.put("heavy intensity drizzle", "сильная морось");
        dictionary.put("light intensity drizzle rain", "слабо моросящий дождь");
        dictionary.put("drizzle rain", "моросящий дождь");
        dictionary.put("heavy intensity drizzle rain", "сильно моросящий дождь");
        dictionary.put("shower rain and drizzle", "ливень и морось");
        dictionary.put("heavy shower rain and drizzle", "сильный ливень и морось");
        dictionary.put("shower drizzle", "сильная морось");
        // thunderstorm
        dictionary.put("thunderstorm with light rain", "гроза, слабый дождь");
        dictionary.put("thunderstorm with rain", "гроза с дождем");
        dictionary.put("thunderstorm with heavy rain", "гроза, сильный дождь");
        dictionary.put("light thunderstorm", "легкая гроза");
        dictionary.put("thunderstorm", "гроза");
        dictionary.put("heavy thunderstorm", "сильная гроза");
        dictionary.put("ragged thunderstorm", "местами грозы");
        dictionary.put("thunderstorm with light drizzle", "гроза, слабая морось");
        dictionary.put("thunderstorm with drizzle", "гроза, морось");
        dictionary.put("thunderstorm with heavy drizzle", "гроза, сильная морось");
    }

}
