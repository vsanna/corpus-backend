package dev.ishikawa.corpus.service.analyzer;

import dev.ishikawa.corpus.domain.FilteredWord;
import dev.ishikawa.corpus.domain.FilteredWord.Reason;
import dev.ishikawa.corpus.error.InvalidRecord;
import dev.ishikawa.corpus.error.RecordNotFoundException;
import dev.ishikawa.corpus.repository.FilteredWordRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AnalyzerServiceImpl implements AnalyzerService {

    private final AnalyzerMaster analyzerMaster;
    private final FilteredWordRepository filteredWordRepository;

    @Override
    @Scheduled(cron = "${cron.analyzerForDailyBatch}", zone = "Asia/Tokyo")
    public void startAnalysisForDaily() {
        analyzerMaster.runForDaily();
    }

    @Override
    @Scheduled(cron = "${cron.analyzerForWeeklyBatch}", zone = "Asia/Tokyo")
    public void startAnalysisForWeekly() {
        analyzerMaster.runForWeekly();
    }

    @Override
    @Scheduled(cron = "${cron.analyzerForMonthlyBatch}", zone = "Asia/Tokyo")
    public void startAnalysisForMonthly() {
        analyzerMaster.runForMonthly();
    }

    @Override
    public List<FilteredWord> getAllFilteredWords() {
        return filteredWordRepository.findAll();
    }

    @Override
    public FilteredWord getFilteredWord(int id) {
        return filteredWordRepository.findById(id).orElseThrow(() -> {
            throw new RecordNotFoundException(FilteredWord.class, id);
        });
    }

    @Override
    public void destroyFilteredWord(int id) {
        filteredWordRepository.findById(id).ifPresentOrElse(filteredWordRepository::delete,
                () -> {
                    throw new RecordNotFoundException(FilteredWord.class, id);
                }
        );
    }

    @Override
    public FilteredWord createFilteredWord(String word) {
        try {
            FilteredWord filteredWord = FilteredWord.builder()
                    .word(word)
                    .reasonCode(Reason.OTHER.getCode())
                    .build();
            filteredWordRepository.saveAndFlush(filteredWord);
            return filteredWordRepository.findById(filteredWord.getId()).get();
            // TODO: ここでcatchするエラーを調べる
        } catch (DuplicateKeyException e) {
            throw new InvalidRecord(FilteredWord.class, e.getMessage());
        }
    }
}
