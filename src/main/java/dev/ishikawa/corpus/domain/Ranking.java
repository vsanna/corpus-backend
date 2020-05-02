package dev.ishikawa.corpus.domain;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

//https://docs.spring.io/spring-data/mongodb/docs/2.2.7.RELEASE/reference/html/#reference
@Document
@Builder
@Data
public class Ranking {
    private String id;
    private LocalDateTime from;
    private LocalDateTime to;
    private Type type;
    private List<Record> mostFrequentRecord;
    private List<Record> leastFrequentRecord;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Type {DAILY, WEEKLY, MONTHLY}

    @Getter
    @Builder
    public static class Record {
        private String word;
        private String originalForm;
        private int freq;
        private List<String> sentences;

        @Override
        public String toString() {
            return "Record{" +
                    "word='" + word + '\'' +
                    ", freq=" + freq +
                    ", sentences=" + sentences +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Ranking{" +
                "id='" + id + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", type=" + type +
                '}';
    }

    // TODO: hookにする
    public Ranking onPrePersist() {
        setCreatedAt(LocalDateTime.now());
        setUpdatedAt(LocalDateTime.now());
        return this;
    }

    public Ranking onPreUpdate() {
        setUpdatedAt(LocalDateTime.now());
        return this;
    }
}
