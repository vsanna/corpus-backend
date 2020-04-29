package dev.ishikawa.corpus.repository;

import dev.ishikawa.corpus.domain.CrawlerArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CrawlerArticlesRepository extends JpaRepository<CrawlerArticle, Integer>,
    JpaSpecificationExecutor<CrawlerArticle> {

}