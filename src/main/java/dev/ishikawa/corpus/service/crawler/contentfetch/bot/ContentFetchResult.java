package dev.ishikawa.corpus.service.crawler.contentfetch.bot;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
class ContentFetchResult {

    private final String title;
    private final String html;
    private final String article;
}