package dev.ishikawa.corpus.service.analyzer;

import dev.ishikawa.corpus.domain.CrawlerArticle;
import dev.ishikawa.corpus.domain.FrequentWord;
import dev.ishikawa.corpus.domain.Ranking;
import dev.ishikawa.corpus.domain.Ranking.Record;
import dev.ishikawa.corpus.domain.Ranking.Record.RecordBuilder;
import dev.ishikawa.corpus.domain.Ranking.Type;
import dev.ishikawa.corpus.domain.Word;
import dev.ishikawa.corpus.infrastructure.mailer.MainMailer;
import dev.ishikawa.corpus.repository.FrequentWordRepository;
import dev.ishikawa.corpus.repository.RankingRepository;
import dev.ishikawa.corpus.repository.file.FileRepository;
import dev.ishikawa.corpus.service.crawler.CrawlerService;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@AllArgsConstructor
public class AnalyzerMaster {

    private final CrawlerService crawlerService;
    private final MainMailer mainMailer;
    private final FrequentWordRepository frequentWordRepository;
    private final FileRepository fileRepository;
    private final RankingRepository rankingRepository;

    @Async
    public void runForDaily() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to = LocalDate.now().atTime(LocalTime.MAX);
        runAnalysis(from, to, Type.DAILY);
    }

    @Async
    public void runForWeekly() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to = from.plusDays(7L).toLocalDate().atTime(LocalTime.MAX);
        runAnalysis(from, to, Type.WEEKLY);
    }

    @Async
    public void runForMonthly() {
        LocalDateTime from = LocalDate.now().atStartOfDay();
        LocalDateTime to = from.plusMonths(1L).toLocalDate().atTime(LocalTime.MAX);
        runAnalysis(from, to, Type.MONTHLY);
    }

    // TODO: filtered_wordsも除外条件に加える
    private List<Record> aggregateToRecords(List<CrawlerArticle> articles, Set<String> frequentWords) {
        log.info("analysis target articles: {}", articles.size());
        return Flux.fromIterable(articles)
                .parallel()
                .map(article -> fileRepository.getObject(article.getWordlistFileName()))
                .filter(Optional::isPresent)
                .map(bytes -> new String(bytes.get(), StandardCharsets.UTF_8))
                .flatMap(stringWordList -> Flux.fromIterable(List.of(stringWordList.split("\n"))))
                .map(Word::fromStringRow)
                .filter(word -> !frequentWords.contains((word.getUniformWord()).toLowerCase()) &&
                        !frequentWords.contains((word.getWord()).toLowerCase()))
                .sequential()
                .toStream()
                .collect(Collectors.groupingBy((word) -> Pair.of(word.getWord(), word.getUniformWord())))
                .entrySet().stream()
                .map(wordWithGroup -> {
                    RecordBuilder recordBuilder = Record.builder();
                    List<String> sentences = wordWithGroup.getValue().stream().map(Word::getSentence).distinct().collect(Collectors.toList());
                    return recordBuilder
                            .word(wordWithGroup.getKey().getFirst())
                            .originalForm(wordWithGroup.getKey().getSecond())
                            .sentences(sentences)
                            .freq(sentences.size())
                            .build();
                })
                .sorted(Comparator.comparingInt(Record::getFreq).reversed())
                .collect(Collectors.toList());
    }

    private void runAnalysis(LocalDateTime from, LocalDateTime to, Type type) {
        log.info("started analyzing {}", type);
        Set<String> frequentWords = frequentWordRepository.findAll().stream()
                .map(FrequentWord::getWord)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        List<Record> recordList = aggregateToRecords(crawlerService.getArticleWithScope(from, to), frequentWords);
        int recordLen = recordList.size();
        if (recordLen < 20) {
            log.error("length of records is less than expected value.");
            return;
        }

        // TODO: remove
        log.info("recordList size: {}", recordList.size());

        int rankingLen = Math.min(recordLen, 50); // TODO: move 590
        Ranking ranking = Ranking.builder()
                .type(type)
                .from(from).to(to)
                .mostFrequentRecord(recordList.subList(0, rankingLen))
                .leastFrequentRecord(recordList.subList(recordList.size() - (rankingLen + 1), recordList.size() - 1))  // TODO: いい感じにする数字ではなくて
                .build()
                .onPrePersist();

        rankingRepository.save(ranking);

        mainMailer.sendAnalysisReport("report", type);
    }
}
