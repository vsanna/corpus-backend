package dev.ishikawa.corpus.repository;

import dev.ishikawa.corpus.domain.CrawlerArticle;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CrawlerArticlesRepository extends JpaRepository<CrawlerArticle, Integer>,
        JpaSpecificationExecutor<CrawlerArticle> {

    // TODO: parseStatusCode?
    List<CrawlerArticle> findByFetchedAtIsBetweenAndParseStatusCode(LocalDateTime from, LocalDateTime to, int parseStatusCode);
}