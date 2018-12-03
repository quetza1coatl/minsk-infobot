package com.quetzalcoatl.telebot.handlers.main;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quetzalcoatl.telebot.handlers.Handler;
import com.quetzalcoatl.telebot.util.Constants;
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
    //TODO переместить их в константы?
    private static final String REGEX = "([А-Яа-я-ё\\.,\\?!]*[\\s]*)*курс[ыау]*(ом)*(ов)*(ами)* валют[ы]*\\b([А-Яа-я-ё\\.,]*[\\s]*)*[\\?\\.!]*";
    private static final String SHORT_MESSAGE = "/exchange";

    private static final String URL_RATES = "http://www.nbrb.by/API/ExRates/Rates/";
    private static final String USD = URL_RATES + "145";
    private static final String EURO = URL_RATES + "292";
    private static final String RUB = URL_RATES + "298";

    @Override
    public boolean isSuitable(String text) {
        return text.equals(Constants.CALLBACK_DATA_EXCHANGE_RATES) || text.equals(SHORT_MESSAGE) || text.toLowerCase().matches(REGEX);
    }

    @Override
    public String getText(Update update) {
        List<URL> urls = getURLList();

        if (urls.isEmpty()) {
            log.error("return null because URL list is empty");
            return null;
        }

        List<Rates> rates = getRatesFromURLList(urls);

        if (rates.isEmpty()) {
            log.error("return null because Rates list is empty");
            return null;
        }

        StringBuilder builder = new StringBuilder()
                .append("Курсы валют Нацбанка РБ \n[")
                .append(rates.get(0).getFormattedDate())
                .append("]\n")
                .append(rates.stream()
                        .map(r -> r.toString())
                        .collect(Collectors.joining("\n")));


        return builder.toString();
    }


    private static List<URL> getURLList() {
        List<URL> urls;
        try {
            urls = Arrays.asList(new URL(USD), new URL(EURO), new URL(RUB));
        } catch (MalformedURLException e) {
            log.error("Can't get URL", e);
            return Collections.EMPTY_LIST;
        }
        return urls;
    }

    private static List<Rates> getRatesFromURLList(@NotNull List<URL> urls) {
        List<Rates> rates = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (URL url : urls) {
            try {
                Rates rate = mapper.readValue(url, Rates.class);
                rates.add(rate);

            } catch (IOException e) {
                log.error("Can't parse date", e);
                return Collections.EMPTY_LIST;
            }
        }

        return rates;
    }

    @JsonAutoDetect
    private static class Rates {
        public Rates() {
        }

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
