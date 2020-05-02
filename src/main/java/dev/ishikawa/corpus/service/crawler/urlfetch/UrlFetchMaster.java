package dev.ishikawa.corpus.service.crawler.urlfetch;

import dev.ishikawa.corpus.domain.Medium;
import dev.ishikawa.corpus.service.crawler.urlfetch.bot.UrlFetchBotFactory;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class UrlFetchMaster {

    @Async
    public void start() {
        // s3でもないしdbでもない。実装と直結するため
        List.of(Medium.values())
                .forEach(medium -> {
                    // TODO: asyncになってる?
                    UrlFetchBotFactory.getUrlFetchBot(medium.getName()).run();
                });
    }
}
