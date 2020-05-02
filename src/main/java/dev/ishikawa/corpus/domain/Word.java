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

    public static Word fromStringRow(String row) {
        String[] tokens = row.split("\t");
        return Word.builder()
                .word(tokens[0].trim())
                .originalForm(tokens[1].trim())
                .type(tokens[2].trim())
                .sentence(tokens[3].replaceAll("__", "\t").trim())
                .build();
    }

    @Override
    public String toString() {
        return "Word{" +
                "word='" + word + '\'' +
                ", originalForm='" + originalForm + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getUniformWord() {
        return this.getOriginalForm().equals("O") ? this.getWord() : this.getOriginalForm();
    }
}
