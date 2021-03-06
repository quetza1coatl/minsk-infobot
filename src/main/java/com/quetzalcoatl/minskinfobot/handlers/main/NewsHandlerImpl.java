package com.quetzalcoatl.minskinfobot.handlers.main;

import com.quetzalcoatl.minskinfobot.handlers.Handler;
import com.quetzalcoatl.minskinfobot.handlers.util.HandlerUtil;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class NewsHandlerImpl implements Handler {

    private static final String HANDLER_NAME = "Сервис новостей";
    private static final String ALIAS = "news";
    private static final String RSS_URL = "https://news.tut.by/rss/all.rss";
    private static final int NUMBER_OF_RSS_RECORDS = 20;
    private static final String DOUBLE_LF = "\n\n";
    private static final String INFO = "*Новости* [tut.by](https://news.tut.by/)" + DOUBLE_LF;
    private static final Logger log = getLogger(NewsHandlerImpl.class);

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
        List<String> rssList = new ArrayList<>();
        try {
            // get list of rss instance
            URL feedSource = new URL(RSS_URL);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));
            // limit the number of entries
            List feedEntries = feed.getEntries().subList(0, NUMBER_OF_RSS_RECORDS);

            // convert rss list to custom objects
            for (Object obj : feedEntries) {
                SyndEntry sEntry = (SyndEntry) obj;
                RssEntry entry = new RssEntry(
                        sEntry.getTitle(),
                        sEntry.getLink(),
                        sEntry.getDescription() == null ? "" : sEntry.getDescription().getValue()
                );
                rssList.add(entry.getFormattedText());
            }
        } catch (Exception e) {
            log.error("Can't parse rss data", e);
        }

        if (rssList.isEmpty()) {
            return null;
        }

        return HandlerUtil.splitMessages(rssList, INFO);
    }

    private static class RssEntry {
        private String title;
        private String link;
        private String description;

        private RssEntry(String title, String link, String description) {
            this.title = title;
            this.link = link;
            this.description = description;
        }


        private String getFormattedText() {
            String formattedDescription = description.replaceAll("<.*?>", "")
                    .replaceAll("\\s+", " ");

            return String.format("[%s](%s)%n%s", title, link, formattedDescription);
        }
    }

}
