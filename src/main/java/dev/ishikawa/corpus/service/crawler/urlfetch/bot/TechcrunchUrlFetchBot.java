package dev.ishikawa.corpus.service.crawler.urlfetch.bot;

import dev.ishikawa.corpus.domain.Medium;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TechcrunchUrlFetchBot implements UrlFetchBot{
    static final private Medium media = Medium.TECH_CRUNCH;

    @Async
    public void run() {
        log.info("run for {}", media.getName());
    }
}
