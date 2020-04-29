package dev.ishikawa.corpus.service.crawler.contentfetch.bot;

import dev.ishikawa.corpus.domain.Medium.MediumName;

public interface ContentFetchBot {

    void run(String url);

    String upload(ContentFetchResult result);

    boolean withThisMedia(MediumName media);
}
