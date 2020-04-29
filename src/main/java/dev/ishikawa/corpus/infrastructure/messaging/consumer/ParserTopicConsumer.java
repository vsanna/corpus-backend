package dev.ishikawa.corpus.infrastructure.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging.TopicName;
import dev.ishikawa.corpus.infrastructure.messaging.message.ParserEvent;
import dev.ishikawa.corpus.service.crawler.CrawlerService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/*
 * ThreadPool
 * 1. CrawlerTopicConsumer
 * 2. AnalyzerTopicConsumer
 * 3. UrlFetcher
 * 4. ContentFetcher
 * 5. Analyzer
 * */


@Slf4j
@Component
@AllArgsConstructor
public class ParserTopicConsumer {

    static final private TopicName topicName = TopicName.PARSER;
    final private ObjectMapper mapper = new ObjectMapper();
    final private CrawlerService crawlerService;

    @PostConstruct
    public void subscribe() {
        log.info("started subscribing ParserTopic");

        // NOTE: 名前付き繰り返し実行
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("parser-topic-consumer-%d").build();
        ScheduledExecutorService executorService = Executors
            .newScheduledThreadPool(10, namedThreadFactory);

        executorService.scheduleAtFixedRate(() -> {
            InMemoryMessaging.consume(topicName).ifPresentOrElse(message -> {
                try {
                    ParserEvent event = mapper.readValue(message, ParserEvent.class);
                    switch (event.getEventType()) {
                        case PARSED_CONTENT:
                            crawlerService.parsedContent(event);
                            break;
                        case FAILED_PARSING_CONTENT:
                            // TODO
                            log.error("failed parsing content");
                            break;
                        default:
                            throw new RuntimeException(
                                String.format("unsupported error {}",
                                    event.getEventType().toString()));
                    }
                } catch (JsonProcessingException e) {
                    log.error("cannot parse ParserEvent message: {}, err: {}", message, e);
                }
            }, () -> {
                log.debug("no message available");
            });
        }, 0, 1, TimeUnit.SECONDS);
    }
}
