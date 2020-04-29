package dev.ishikawa.corpus.service.crawler.urlfetch.bot;

import dev.ishikawa.corpus.domain.Medium;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging.TopicName;
import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

    // TODO: visite site and get urls
    private List<String> getUrls() {
        return new ArrayList<>(List.of(
            "https://issus.me/5",
            "https://issus.me/6",
            "https://issus.me/7",
            "https://issus.me/8"
        ));
    }
}
