package dev.ishikawa.corpus.service.crawler;

import dev.ishikawa.corpus.domain.CrawlerArticle;
import dev.ishikawa.corpus.infrastructure.messaging.message.ParserEvent;
import java.time.LocalDateTime;
import java.util.List;

public interface CrawlerService {

    void fetchUrls();

    void parsedContent(ParserEvent event);

    List<CrawlerArticle> getArticleWithScope(LocalDateTime from, LocalDateTime to);
}
