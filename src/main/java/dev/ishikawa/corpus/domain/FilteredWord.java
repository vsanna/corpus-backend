package dev.ishikawa.corpus.domain;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Table(name = "filtered_words")
@Data
@Entity
@Builder
public class FilteredWord extends ModelBase {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "word", nullable = false)
    private String word;

    @Column(name = "reason", nullable = false)
    private Integer reasonCode = 0;

    // TODO: このmappingを省略したい
    public Reason getReason() {
        return List.of(Reason.values()).stream()
                .filter(status -> status.getCode() == reasonCode)
                .findFirst()
                .orElseThrow(() -> {
                    // TODO: appを壊してほしい
                    throw new RuntimeException("invalid status");
                });
    }


    @Getter
    public enum Reason {
        OTHER(0);

        private final int code;

        Reason(int code) {
            this.code = code;
        }
    }

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // TODO: move to base class
    // TODO: ここでないと読み込めない
    @PrePersist
    public void onPrePersist() {
        setCreatedAt(LocalDateTime.now());
        setUpdatedAt(LocalDateTime.now());
    }
}