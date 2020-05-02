package dev.ishikawa.corpus.service.analyzer;

import dev.ishikawa.corpus.domain.FilteredWord;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AnalyzerService {

    void startAnalysisForDaily();

    void startAnalysisForWeekly();

    void startAnalysisForMonthly();

    FilteredWord createFilteredWord(String word);

    List<FilteredWord> getAllFilteredWords();

    FilteredWord getFilteredWord(int id);

    void destroyFilteredWord(int id);
}
