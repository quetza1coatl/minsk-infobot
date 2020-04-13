package com.quetzalcoatl.minskinfobot.handlers.main;

import com.quetzalcoatl.minskinfobot.handlers.Handler;
import com.quetzalcoatl.minskinfobot.handlers.util.HandlerUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class MovieHandlerImpl implements Handler {

    private static final String HANDLER_NAME = "Сервис киноафиши";
    private static final String ALIAS = "movie";
    private static final String MOVIE_URL = "https://afisha.tut.by/film/";
    private static final int NUMBER_OF_MOVIE_RECORDS = 20;
    private static final String INFO = "*Киноафиша Минска* [tut.by](https://afisha.tut.by/film/)\n\n";
    private static final Logger log = getLogger(MovieHandlerImpl.class);

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
        final String CLASS = "class";
        Document document = null;
        try {
            document = Jsoup.connect(MOVIE_URL).get();

        } catch (IOException e) {
            log.error("Can't get DOM model from movie url", e);
        }

        if (document == null) {
            return null;
        }
        // Array of films are divided to rows
        Elements rows = document.getElementsByAttributeValue(CLASS, "b-lists list_afisha col-5");
        List<Element> items = new ArrayList<>();
        // Each row contains a list of films
        rows.forEach(row -> items.addAll(row.getElementsByAttributeValue(CLASS, "lists__li ")));
        List<String> movies = items.stream()
                .limit(NUMBER_OF_MOVIE_RECORDS)
                .map(item -> String.format("[%s](%s)%s%s",
                        // title
                        item.getElementsByAttributeValue(CLASS, "name").first().getElementsByTag("span").text(),
                        // link
                        item.getElementsByTag("a").first().attr("href"),
                        // description.Can be empty
                        item.getElementsByAttributeValue(CLASS, "txt").first().getElementsByTag("p").text().isEmpty() ?
                                "" : "\n" + item.getElementsByAttributeValue(CLASS, "txt").first().getElementsByTag("p").text(),
                        // raiting. Can be empty
                        item.getElementsByAttributeValue(CLASS, "raiting hot").text().isEmpty() ?
                                "" : "\nРейтинг: *" + item.getElementsByAttributeValue(CLASS, "raiting hot").text() + "*"))
                .collect(Collectors.toList());

        return HandlerUtil.splitMessages(movies, INFO);
    }

}
