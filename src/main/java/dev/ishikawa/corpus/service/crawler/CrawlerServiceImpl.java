package dev.ishikawa.corpus.service.crawler;

import dev.ishikawa.corpus.domain.ParseStatus;
import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import dev.ishikawa.corpus.infrastructure.messaging.message.ParserEvent;
import dev.ishikawa.corpus.repository.CrawlerArticlesRepository;
import dev.ishikawa.corpus.service.crawler.urlfetch.UrlFetchMaster;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
@Service
public class CrawlerServiceImpl implements CrawlerService {

    private final UrlFetchMaster urlFetchMaster;
    private final CrawlerArticlesRepository crawlerArticlesRepository;

    @Override
    public void fetchUrls() {
        urlFetchMaster.start();
    }

    @Override
    @Transactional
    public void parsedContent(ParserEvent event) {
        log.info("parsed content: {} {}", event.getEventType(),
            event.getPayload().get("articleId"));
        
        int articleId = Integer.valueOf(event.getPayload().get("articleId"));
        crawlerArticlesRepository.findById(articleId).ifPresentOrElse((article) -> {
            article.setParseStatusCode(ParseStatus.COMPLETED.getCode());
        }, () -> {
            // invalid situation.
            log.error("既にpendingから更新済み");
            throw new RuntimeException("already updated parse_status");
        });
    }

    @Override
    public void fetchedContent(CrawlerEvent event) {
        log.info("fetched content: {} {}", event.getEventType(), event.getMediumName());
    }
}
