package dev.ishikawa.corpus.domain;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

@Slf4j
@Getter
public enum Medium {

    // TODO: refactor: Mediumを単に文字列の集合とし、url取得などのロジックは別クラスにする
    //
    NEWYORK_TIMES(MediumName.NEWYORK_TIMES, "https://www.nytimes.com", "/", 0),
    TECH_CRUNCH(MediumName.TECH_CRUNCH, "https://techcrunch.com", "/startups", 1000);

    public enum MediumName {NEWYORK_TIMES, TECH_CRUNCH}

    private final MediumName name;
    private final URI baseUri;
    private final String path;
    private final int code;

    Medium(MediumName name, String baseUrl, String path, int code) {
        this.name = name;
        this.code = code;

        try {
            this.baseUri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            // TODO: RuntimeExceptionでなく起動防いでほしい
            LoggerFactory.getLogger(Medium.class).error("uri syntax is invalid", e);
            throw new RuntimeException("uri syntax is invalid");
        }
        this.path = path;
    }

    public URI getIndexPageUrl() {
        String newPath = baseUri.getPath() + "/" + path;
        return baseUri.resolve(newPath);
    }
}
