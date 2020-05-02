package dev.ishikawa.corpus.service.analyzer;

import org.springframework.stereotype.Service;

@Service
public interface AnalyzerService {

    void startAnalysisForDaily();

    void startAnalysisForWeekly();

    void startAnalysisForMonthly();
}
