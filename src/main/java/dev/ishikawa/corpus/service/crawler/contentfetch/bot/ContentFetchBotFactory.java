package dev.ishikawa.corpus.service.crawler.contentfetch.bot;

import dev.ishikawa.corpus.domain.Medium.MediumName;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ContentFetchBotFactory {

//    @Autowired
//    private NewyorkTimesContentFetchBot newyorkTimesContentFetchBot;
//
//    @Autowired
//    private TechcrunchContentFetchBot techcrunchContentFetchBot;

    // NOTE: SpringのAutowireを利用したFactoryパターン. 実装のAutowireはできない
    @Autowired
    List<ContentFetchBot> contentFetchBots;

    public ContentFetchBot getContentFetchBot(MediumName mediumName) {
        return contentFetchBots.stream()
            .filter(bot -> bot.withThisMedia(mediumName)).findFirst()
            .orElseThrow(() -> {
                throw new RuntimeException("Unsupported medium type");
            });
    }
}
