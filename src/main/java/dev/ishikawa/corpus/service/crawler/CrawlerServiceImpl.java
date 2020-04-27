package dev.ishikawa.corpus.service.crawler;

import dev.ishikawa.corpus.service.crawler.contentfetch.ContentFetchMaster;
import dev.ishikawa.corpus.service.crawler.urlfetch.UrlFetchMaster;
import dev.ishikawa.corpus.service.port.CrawlerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CrawlerServiceImpl implements CrawlerService {
    private final UrlFetchMaster urlFetchMaster;
    private final ContentFetchMaster contentFetchMaster;

    @Override
    public void fetchUrls() {
        urlFetchMaster.start();
    }

    @Override
    public void fetchContent() {
        contentFetchMaster.start();
    }
}
