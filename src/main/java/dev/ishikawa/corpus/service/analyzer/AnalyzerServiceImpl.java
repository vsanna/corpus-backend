package dev.ishikawa.corpus.service.analyzer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AnalyzerServiceImpl implements AnalyzerService {

    private final AnalyzerMaster analyzerMaster;

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
}
