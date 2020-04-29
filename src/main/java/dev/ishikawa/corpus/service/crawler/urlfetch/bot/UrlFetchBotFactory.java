package dev.ishikawa.corpus.service.crawler.urlfetch.bot;

import dev.ishikawa.corpus.domain.Medium.MediumName;
import org.springframework.stereotype.Service;

@Service
public class UrlFetchBotFactory {

    static public UrlFetchBot getUrlFetchBot(MediumName mediumName) {
        switch (mediumName) {
            case NEWYORK_TIMES:
                return new NewyorkTimesUrlFetchBot();
            case TECH_CRUNCH:
                return new TechcrunchUrlFetchBot();
            default:
                throw new RuntimeException("Unsupported medium type");
        }
    }
}
