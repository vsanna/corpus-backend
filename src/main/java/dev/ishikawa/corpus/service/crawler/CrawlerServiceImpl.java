package dev.ishikawa.corpus.service.crawler;

import dev.ishikawa.corpus.domain.CrawlerArticle;
import dev.ishikawa.corpus.domain.ParseStatus;
import dev.ishikawa.corpus.infrastructure.messaging.message.ParserEvent;
import dev.ishikawa.corpus.repository.CrawlerArticlesRepository;
import dev.ishikawa.corpus.service.crawler.urlfetch.UrlFetchMaster;
import java.time.LocalDateTime;
import java.util.List;
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
        switch (event.getEventType()) {
            case PARSED_CONTENT:
                log.info("parsed content: {} {}", event.getEventType(), event.getPayload().get("articleId"));

                int articleId = Integer.parseInt(event.getPayload().get("articleId"));
                crawlerArticlesRepository.findById(articleId).ifPresentOrElse((article) -> {
                    article.setParseStatusCode(ParseStatus.COMPLETED.getCode());
                }, () -> {
                    // invalid situation.
                    log.error("既にpendingから更新済み");
                    throw new RuntimeException("already updated parse_status");
                });
                break;
            case FAILED_PARSING_CONTENT:
                log.error("failed parsing content");
                break;
            default:
                throw new RuntimeException("unsupported event type");
        }
    }

    @Override
    public List<CrawlerArticle> getArticleWithScope(LocalDateTime from, LocalDateTime to) {
        // TODO: mapping
        return crawlerArticlesRepository.findByFetchedAtIsBetweenAndParseStatusCode(from, to, ParseStatus.COMPLETED.getCode());
    }
}
