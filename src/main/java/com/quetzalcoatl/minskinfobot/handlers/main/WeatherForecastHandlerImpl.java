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
    private static Map<String, String> emojiMap;
    private static Map<String, String> dictionary;
    private static final Logger log = getLogger(WeatherForecastHandlerImpl.class);

    static {
        fillEmojiMap();
        fillDictionary();
    }

    @Override
    public String getAlias(){
        return ALIAS;
    }

    @Override
    public String getHandlerName(){
        return HANDLER_NAME;
    }

    @Override
    public final String getText(Update update) {
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

        //TODO: delete after tests
        log.info("User {}. Cache: Response was got from server",  update.getMessage().getFrom().getFirstName());
        return INFO + result;
    }

    private static class Weather {

        private static final String CELSIUS = "\u2103";
        private static final String WIND = "\uD83C\uDF2C";
        private static final String HUMIDITY = "\uD83D\uDCA7";
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
            DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE").withLocale(Locale.forLanguageTag("RU"));
            String formattedDateTime = String.format("_%s %s (%s)_",
                    dateTime.format(timeFormatter), dateTime.format(dayFormatter), dateTime.format(dateFormatter));

            // get processed weather emoji
            String weatherEmoji = getMainWeatherEmoji();

            // format temperature. Null-check from Json
            String formattedTemperature =
                    temperature != Integer.MIN_VALUE ? "*" + temperature + CELSIUS + "*" : "temp: N/A";
            if (temperature > 0) {
                formattedTemperature = "+" + formattedTemperature;
            }

            // description. Null-check from Json. Getting data from dictionary
            String formattedDescription = description.equals("null") ?
                    "description: N/A" : dictionary.getOrDefault(description, description);

            //  cloudiness. Null-check from Json
            String formattedCloudiness =
                    cloudiness != Integer.MIN_VALUE ? CLOUDINESS + cloudiness + "%" : CLOUDINESS + "N/A";

            // pressure. Null-check from Json
            String formattedPressure =
                    pressure != Integer.MIN_VALUE ? PRESSURE + pressure + " гПа" : PRESSURE + "N/A";

            // humidity. Null-check from Json
            String formattedHumidity =
                    humidity != Integer.MIN_VALUE ? HUMIDITY + humidity + "%" : HUMIDITY + "N/A";

            // wind. Null-check from Json
            String formattedWind =
                    windSpeed != Integer.MIN_VALUE ? WIND + windSpeed + " м/сек" : WIND + "N/A";

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
            }

            return emojiMap.getOrDefault(result, "");
        }
    }

    // if key start with uppercase - this value used for API main type of weather and can be got by field Weather.general,
    // otherwise - a) value used for API subtype and can be got by field Weather.description,
    // b) it's a custom value (with prefix 'custom')
    private static void fillEmojiMap() {
        emojiMap = new HashMap<String, String>() {{
            put("custom: sun with rain", "\uD83C\uDF26");
            put("sun", "\u2600");
            put("moon", "\uD83C\uDF1A");
            put("Clouds", "\u2601");
            put("few clouds", "\uD83C\uDF24");
            put("scattered clouds", "\uD83C\uDF25");
            put("broken clouds", "\uD83C\uDF25");
            put("overcast clouds", "\u2601");
            put("Rain", "\uD83C\uDF27");
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

    private static void fillDictionary() {
        dictionary = new HashMap<String, String>() {{
            // rain
            put("light rain", "слабый дождь");
            put("moderate rain", "умеренный дождь");
            put("heavy intensity rain", "сильный дождь");
            put("very heavy rain", "очень сильный дождь");
            put("extreme rain", "ливень");
            put("freezing rain", "ледяной дождь");
            put("light intensity shower rain", "незначительный ливень");
            put("shower rain", "ливень");
            put("heavy intensity shower rain", "сильный ливень");
            put("ragged shower rain", "местами ливни");
            // snow
            put("light snow", "слабый снег");
            put("Snow", "снег");
            put("Heavy snow", "сильный снег");
            put("Sleet", "мокрый снег");
            put("Light shower sleet", "слабый мокрый снег");
            put("Shower sleet", "сильный мокрый снег");
            put("Rain and snow", "снег с дождем");
            put("Light shower snow", "слабый снег с дождем");
            put("Shower snow", "снегопад");
            put("Heavy shower snow", "сильный снегопад");
            // cloudiness
            put("clear sky", "безоблачно");
            put("few clouds", "незначительная облачность");
            put("scattered clouds", "средняя облачность");
            put("broken clouds", "значительная облачность");
            put("overcast clouds", "сплошная облачность");
            //atmosphere
            put("mist", "туман");
            put("Smoke", "дымка");
            put("Haze", "дымка");
            put("fog", "туман");
            put("squalls", "шквал");
            // drizzle
            put("light intensity drizzle", "слабая морось");
            put("drizzle", "морось");
            put("heavy intensity drizzle", "сильная морось");
            put("light intensity drizzle rain", "слабо моросящий дождь");
            put("drizzle rain", "моросящий дождь");
            put("heavy intensity drizzle rain", "сильно моросящий дождь");
            put("shower rain and drizzle", "ливень и морось");
            put("heavy shower rain and drizzle", "сильный ливень и морось");
            put("shower drizzle", "сильная морось");
            // thunderstorm
            put("thunderstorm with light rain", "гроза, слабый дождь");
            put("thunderstorm with rain", "гроза с дождем");
            put("thunderstorm with heavy rain", "гроза, сильный дождь");
            put("light thunderstorm", "легкая гроза");
            put("thunderstorm", "гроза");
            put("heavy thunderstorm", "сильная гроза");
            put("ragged thunderstorm", "местами грозы");
            put("thunderstorm with light drizzle", "гроза, слабая морось");
            put("thunderstorm with drizzle", "гроза, морось");
            put("thunderstorm with heavy drizzle", "гроза, сильная морось");

        }};
    }

}
