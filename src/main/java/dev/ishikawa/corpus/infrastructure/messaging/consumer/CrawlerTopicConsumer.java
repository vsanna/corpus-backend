package dev.ishikawa.corpus.infrastructure.messaging.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging.TopicName;
import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import dev.ishikawa.corpus.service.crawler.CrawlerService;
import dev.ishikawa.corpus.service.crawler.contentfetch.ContentFetchMaster;
import dev.ishikawa.corpus.service.parser.ParserService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CrawlerTopicConsumer {

    static final private TopicName topicName = TopicName.CRAWLER;
    final private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    final private ContentFetchMaster contentFetchMaster;

    @Autowired
    final private CrawlerService crawlerService;

    @Autowired
    final private ParserService parserService;

    @PostConstruct
    public void subscribe() {
        log.info("started subscribing CrawlerTopic");

        // NOTE: 名前付き繰り返し実行
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("crawler-topic-consumer-%d").build();
        ScheduledExecutorService executorService = Executors
            .newScheduledThreadPool(10, namedThreadFactory);

        executorService.scheduleAtFixedRate(() -> {
            InMemoryMessaging.consume(topicName).ifPresentOrElse(message -> {
                try {
                    CrawlerEvent event = mapper.readValue(message, CrawlerEvent.class);
                    switch (event.getEventType()) {
                        case FETCHED_CONTENT:
                            crawlerService.fetchedContent(event);
                            parserService.fetchedContent(event);
                            break;
                        case FETCHED_URL:
                            // TODO/NOTE: urlFetcherと微妙に違う。予めstartさせたところにこれを渡したい。
                            contentFetchMaster.runSingleTask(event);
                            break;
                        case FAILED_FETCHING_CONTENT:
                            // TODO: error logだしてretryする処理をcrawlerserviceに任せる
                            log.error("failed fetching content");
                            break;
                        default:
                            throw new RuntimeException(
                                String.format("unsupported error {}",
                                    event.getEventType().toString()));
                    }
                } catch (JsonProcessingException e) {
                    log.error("cannot parse CrawlerEvent message: {}, err: {}", message, e);
                }
            }, () -> {
                log.debug("no message available");
            });
        }, 0, 1, TimeUnit.SECONDS);
    }
}
