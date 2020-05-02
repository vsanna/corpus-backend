package dev.ishikawa.corpus.service.crawler.urlfetch.bot;

import dev.ishikawa.corpus.domain.Medium;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging.TopicName;
import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NewyorkTimesUrlFetchBot implements UrlFetchBot {

    static final private Medium medium = Medium.NEWYORK_TIMES;

    @Async
    public void run() {
        log.info("getting urls for {}", medium.getName());
        getUrls().forEach(url -> {
            InMemoryMessaging.publish(
                    TopicName.CRAWLER,
                    CrawlerEvent.fetchedUrl(medium.getName(), url).toMessage());
        });
    }

    private List<String> getUrls() {
        List<String> urls = new ArrayList<>();

        Document doc = null;
        try {
            doc = Jsoup.connect(medium.getIndexPageUrl().toString()).get();
        } catch (IOException e) {
            log.error("cannot get content to {}, url: {}", medium.getName(),
                    medium.getIndexPageUrl(), e);
            // TODO: エラーを上げてFAILED_FETCHING_CONTENTを投げる
            return Collections.emptyList();
        }

        // 各記事のaタグを取得。jQueryのセレクターと同じ感じで記載
        return doc.select("#stream-panel li a")
                .stream()
                .map(headline -> headline.absUrl("href"))
                .filter(url -> url.startsWith(medium.getBaseUri().toString()))
                .collect(Collectors.toList());
    }
}
