package dev.ishikawa.corpus.domain;

import lombok.Getter;

@Getter
public enum Medium {
    NEWYORK_TIMES("newyork times", "https://www.nytimes.com", "/"),
    TECH_CRUNCH("techcrunch", "https://techcrunch.com", "/startups");

    private String name;
    private String baseUrl;
    private String path;

    Medium(String name, String baseUrl, String path) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.path = path;
    }
}
