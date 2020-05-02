package dev.ishikawa.corpus.service.parser;

import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ParserServiceImpl implements ParserService {

    private ParserMaster parserMaster;

    @Override
    public void createWordList(CrawlerEvent event) {
        parserMaster.start(Integer.valueOf(event.getPayload().get("articleId")));
    }
}
