package dev.ishikawa.corpus.service.analyzer;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnalyzerServiceImpl implements AnalyzerService {

    private AnalyzerMaster analyzerMaster;

    @Override
    @Scheduled(cron = "${cron.analyzerBatch}", zone = "Asia/Tokyo")
    public void startAnalysis() {
        analyzerMaster.run();
    }
}
