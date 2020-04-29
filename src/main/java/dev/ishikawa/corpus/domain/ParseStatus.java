package dev.ishikawa.corpus.domain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum ParseStatus {
    PENDING(0),
    COMPLETED(1000),
    FAILURED(2000);

    private final int code;

    ParseStatus(int code) {
        this.code = code;
    }
}
