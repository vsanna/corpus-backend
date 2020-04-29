package dev.ishikawa.corpus.service.crawler.contentfetch;

import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import dev.ishikawa.corpus.service.crawler.contentfetch.bot.ContentFetchBotFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ContentFetchMaster {

    @Autowired
    final private ContentFetchBotFactory contentFetchBotFactory;

    public void start() {
    }

    public void runSingleTask(CrawlerEvent event) {
        // TODO: 以下処理をasyncで別スレッドに渡す
        // TODO: 型安全
        contentFetchBotFactory.getContentFetchBot(event.getMediumName())
            .run(event.getPayload().get("url"));
    }
}
