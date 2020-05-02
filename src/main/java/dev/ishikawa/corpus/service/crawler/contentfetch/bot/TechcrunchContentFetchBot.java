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
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Component
@Slf4j
public class TechcrunchContentFetchBot implements ContentFetchBot {

    private final FileRepository fileRepository;
    private final CrawlerArticlesRepository crawlerArticlesRepository;
    static final private Medium media = Medium.TECH_CRUNCH;

    // TODO: 実装が重複. getContentの箇所だけ取り替えられるStrategyにする
    // TODO: async chainにする
    @Async
    public void run(String url) {
        log.info("getting content for {}", url);

        ContentFetchResult result = getContent(url);
        String htmlFilename = upload(result);

        CrawlerArticle article = CrawlerArticle.builder()
                .title(result.getTitle())
                .htmlFileName(htmlFilename)
                .url(url)
                .body(result.getArticle())
                .mediaTypeCode(media.getCode()) // TODO: このmapping手でやりたくない
                .fetchedAt(LocalDateTime.now())
                .parseStatusCode(ParseStatus.PENDING.getCode()) // TODO: このmapping手でやりたくない
                .build();

        saveAndPublish(article);
    }

    public String upload(ContentFetchResult result) {
//        System.out.println(LocalDateTime.now()); // 2020-05-01T10:41:22.152427
//        System.out.println(LocalDateTime.now(ZoneId.of("Asia/Tokyo"))); // 2020-05-01T10:41:22.152427
//        System.out.println(LocalDateTime.now(ZoneId.of("UTC"))); // 2020-05-01T01:41:22.156035
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

    private ContentFetchResult getContent(String url) {
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("couldn't parse", e);
            throw new RuntimeException("couldn't get full article html");
        }

        String html = doc.html();

        String title = "";
        Elements titleTag = doc.getElementsByTag("title");
        if (titleTag.size() > 0) {
            title = titleTag.get(0).text();
        }

        String article = "";
        Elements articleTag = doc.select(".article-content");
        if (articleTag.size() > 0) {
            article = articleTag.get(0).text();
        }
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
