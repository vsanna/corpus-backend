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

/*
 * - 担当するmediaに対しアクセスし、
 * - urlを取得し、
 * - そのurlをpublishする
 * */
@Component
@Slf4j
public class TechcrunchUrlFetchBot implements UrlFetchBot {

    static final private Medium mediun = Medium.TECH_CRUNCH;

    @Async
    public void run() {
        // TODO: ここがmain出ないことを確認
        log.info("getting urls for {}", mediun.getName());

        getUrls().forEach(url -> {
            InMemoryMessaging.publish(
                TopicName.CRAWLER,
                CrawlerEvent.fetchedUrl(mediun.getName(), url).toMessage());
        });
    }

    // TODO: visite site and get urls
    private List<String> getUrls() {
        return new ArrayList<>(List.of(
            "https://issus.me/1",
            "https://issus.me/2",
            "https://issus.me/3",
            "https://issus.me/4"
        ));
    }
}
