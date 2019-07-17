package com.quetzalcoatl.minskinfobot.handlers.main;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quetzalcoatl.minskinfobot.handlers.Handler;
import com.quetzalcoatl.minskinfobot.util.InfoType;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class ExchangeRateHandlerImpl implements Handler {

    private static final Logger log = getLogger(ExchangeRateHandlerImpl.class);

    private static final String REGEX = "([А-Яа-я-ё.,?!]*[\\s]*)*курс[ыау]*(ом)*(ов)*(ами)* валют[ы]*\\b([А-Яа-я-ё.,]*[\\s]*)*[?.!]*";
    /**
     * Command starting with '/' symbol and can be registered by BotFarther in list of commands
     */
    private static final String COMMAND_MESSAGE = "/exchangerates";

    private static final String URL_RATES = "http://www.nbrb.by/API/ExRates/Rates/";
    private static final String USD = URL_RATES + "145";
    private static final String EURO = URL_RATES + "292";
    private static final String RUB = URL_RATES + "298";

    @Override
    public boolean isSuitable(String text) {
        return text.equals(InfoType.EXCHANGE_RATES.value) || text.equals(COMMAND_MESSAGE) || text.toLowerCase().matches(REGEX);
    }

    @Override
    public String getText(Update update) {
        List<URL> urls = getURLList();

        if (urls.isEmpty()) {
            log.error("returns null because URL list is empty");
            return null;
        }

        List<Rates> rates = getRatesFromURLList(urls);

        if (rates.isEmpty()) {
            log.error("returns null because Rates list is empty");
            return null;
        }

        return "".concat("Курсы валют Нацбанка РБ \n[")
                .concat(rates.get(0).getFormattedDate())
                .concat("]\n")
                .concat(rates.stream()
                        .map(Rates::toString)
                        .collect(Collectors.joining("\n")));
    }


    private static List<URL> getURLList() {
        List<URL> urls;
        try {
            urls = Arrays.asList(new URL(USD), new URL(EURO), new URL(RUB));
        } catch (MalformedURLException e) {
            log.error("Can't get URL", e);
            return new ArrayList<>();
        }
        return urls;
    }

    private static List<Rates> getRatesFromURLList(@NotNull List<URL> urls) {
        List<Rates> rates = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        //TODO stream?
        for (URL url : urls) {
            try {
                Rates rate = mapper.readValue(url, Rates.class);
                rates.add(rate);

            } catch (IOException e) {
                log.error("Can't parse date", e);
                return new ArrayList<>();
            }
        }

        return rates;
    }

    @JsonAutoDetect
    private static class Rates {

        public Rates() {}


        @JsonProperty("Cur_ID")
        public String id;
        @JsonProperty("Date")
        public Date date;
        @JsonProperty("Cur_Abbreviation")
        public String abbr;
        @JsonProperty("Cur_Scale")
        public int scale;
        @JsonProperty("Cur_Name")
        public String name;
        @JsonProperty("Cur_OfficialRate")
        public double rate;

        private String getFormattedDate() {
            return new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH).format(this.date);
        }

        @Override
        public String toString() {
            return String.format("%d %s = %.4f BYN", this.scale, this.name, this.rate);
        }

    }

}
