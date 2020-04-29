package dev.ishikawa.corpus.service.crawler.contentfetch.bot;

import dev.ishikawa.corpus.domain.CrawlerArticle;
import dev.ishikawa.corpus.domain.Medium;
import dev.ishikawa.corpus.domain.Medium.MediumName;
import dev.ishikawa.corpus.domain.ParseStatus;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging.TopicName;
import dev.ishikawa.corpus.infrastructure.messaging.message.CrawlerEvent;
import dev.ishikawa.corpus.repository.CrawlerArticlesRepository;
import dev.ishikawa.corpus.repository.file.FileRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Component
@Slf4j
public class NewyorkTimesContentFetchBot implements ContentFetchBot {

    private final FileRepository fileRepository;
    private CrawlerArticlesRepository crawlerArticlesRepository;
    static final private Medium media = Medium.NEWYORK_TIMES;

    @Async
    public void run(String url) {
        log.info("getting content for {}", media.getName());

        // TODO: asyncにしてみる
        ContentFetchResult result = getContent();
        String htmlFilename = upload(result);

        CrawlerArticle article = CrawlerArticle.builder()
            .title(result.getTitle())
            .url(url)
            .body(result.getArticle())
            .htmlFileName(htmlFilename)
            .mediaTypeCode(media.getCode()) // TODO: このmapping手でやりたくない
            .fetchedAt(LocalDateTime.now())
            .parseStatusCode(ParseStatus.PENDING.getCode()) // TODO: このmapping手でやりたくない
            .build();

        saveAndPublish(article);
    }

    public String upload(ContentFetchResult result) {
        DateTimeFormatter yyyy_mm_dd_hh_mm_ss = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss");
        String currentAt = LocalDateTime.now().format(yyyy_mm_dd_hh_mm_ss);
        String filename = String.format("%s_%s.html", media.getName().toString(), currentAt);
        fileRepository.putObject(filename, result.getHtml().getBytes());
        return filename;
    }

    @Override
    public boolean withThisMedia(MediumName mediaName) {
        return media.getName() == mediaName;
    }

    private ContentFetchResult getContent() {
        // TODO: visite site and get urls
        String title = "title1";
        String html = "<html><head><title>hoge</title></head><body><article><h1>h1 1</h1>this is so nice table. I'd like to use this one...</article></body></html>";
        String article = "This is so nice table. I guess that so experienced craftman made this.";
        return ContentFetchResult.builder()
            .title(title)
            .html(html)
            .article(article)
            .build();
    }


    @Transactional
    void saveAndPublish(CrawlerArticle article) {
        // TODO: validationをどうすればいいのか
        // TODO: あえてnonullの値をnullにして壊してみる
        crawlerArticlesRepository.saveAndFlush(article);

        // NOTE: これは型安全ではないがサービス間疎通をシミュレーションしているので仕方ない
        InMemoryMessaging.publish(
            TopicName.CRAWLER,
            CrawlerEvent.fetchedContent(media.getName(), article.getId()).toMessage());
    }
}
