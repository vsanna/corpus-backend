package dev.ishikawa.corpus.service.parser;

import dev.ishikawa.corpus.domain.CrawlerArticle;
import dev.ishikawa.corpus.domain.Word;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging.TopicName;
import dev.ishikawa.corpus.infrastructure.messaging.message.ParserEvent;
import dev.ishikawa.corpus.repository.CrawlerArticlesRepository;
import dev.ishikawa.corpus.repository.file.FileRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
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
    private final FileRepository fileRepository;

    public void start(int articleId) {
        // TODO: fetched_atが対象期間 & parse_status completedで絞る
        // TODO: このrepoへのアクセスはCrawlerServiceを経由するべき
        crawlerArticlesRepository.findById(articleId).ifPresent(article -> {
            List<Word> words = parserWorker.run(article);
            upload(article, words);
            InMemoryMessaging.publish(
                TopicName.PARSER,
                ParserEvent.parsedContent(articleId).toMessage());
        });

    }

    private void upload(CrawlerArticle article, List<Word> words) {
        String wordListBody = words.stream().map(Word::toRow).collect(Collectors.joining("\n"));
        // TODO: fix
        DateTimeFormatter yyyy_mm_dd_hh_mm_ss = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss");
        String currentAt = LocalDateTime.now().format(yyyy_mm_dd_hh_mm_ss);
        String filename = String.format("%s_%s.tsv", article.getMediaName(), currentAt);
        fileRepository.putObject(filename, wordListBody.getBytes());
    }
}
