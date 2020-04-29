package dev.ishikawa.corpus.domain;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Builder
public class Word {

    private String word;
    private String originalForm;
    private String sentence;
    // TODO change this into enum
    private String type;

    public String toRow() {
        return String.join(
            "\t",
            List.of(word, originalForm, type, sentence.replaceAll("\t", "__"))
        );
    }
}
