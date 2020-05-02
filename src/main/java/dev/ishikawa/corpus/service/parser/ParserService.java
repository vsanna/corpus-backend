package dev.ishikawa.corpus.service.parser;

import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import org.springframework.stereotype.Service;

@Service
public interface ParserService {

    void createWordList(CrawlerEvent event);
}
