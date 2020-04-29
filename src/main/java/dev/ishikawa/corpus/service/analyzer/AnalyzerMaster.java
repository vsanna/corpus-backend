package dev.ishikawa.corpus.service.analyzer;

import dev.ishikawa.corpus.repository.CrawlerArticlesRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rx.internal.util.SuppressAnimalSniffer;

@Slf4j
@Component
@AllArgsConstructor
public class AnalyzerMaster {

    private final CrawlerArticlesRepository crawlerArticlesRepository;

    @SuppressAnimalSniffer
    public void run() {
        // TODO: crawlerServiceから対象期間のwordlist_filenameを取得
        //  (fetched_atが対象期間 & parse_status completedで絞る)
        // TODO: このrepoへのアクセスはCrawlerServiceを経由するべき
        // TODO: asyncと待ち合わせにする(Reactorでもいいかもここは)
        crawlerArticlesRepository.findAll().stream().forEach(article -> {
            // 並列で集計とフィルタリングしてrankingを作る
            log.info("starting analysis....");
        });
    }
}
