package dev.ishikawa.corpus.service.parser;

import dev.ishikawa.corpus.repository.CrawlerArticlesRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// TODO: 単語に分解する
@Slf4j
@Component
@AllArgsConstructor
public class ParserMaster {

    private final CrawlerArticlesRepository crawlerArticlesRepository;
    private final ParserWorker parserWorker;

    public void start(int articleId) {
        // TODO: このrepoへのアクセスはCrawlerServiceを経由するべき
        crawlerArticlesRepository.findById(articleId).ifPresent(parserWorker::run);
    }

}
