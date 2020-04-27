package dev.ishikawa.corpus.service.crawler.urlfetch;

import dev.ishikawa.corpus.domain.Medium;
import dev.ishikawa.corpus.service.crawler.urlfetch.bot.UrlFetchBotFactory;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UrlFetchMaster {
    public void start() {
        // 1. get media list
        // s3でもないしdbでもない。実装と直結するから
        List.of(Medium.values())
            // 2. それぞれに合わせたcrawlerをfactoryから取得しasyncにわたす
            .forEach(medium -> {
                UrlFetchBotFactory.getUrlFetchBot(medium).run();
        });
    }
}
