package dev.ishikawa.corpus.service.crawler;

import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import dev.ishikawa.corpus.infrastructure.messaging.message.ParserEvent;

public interface CrawlerService {

    void fetchUrls();

    void fetchedContent(CrawlerEvent event);

    void parsedContent(ParserEvent event);
}
