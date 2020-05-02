package dev.ishikawa.corpus.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Data;

@Data
public class ModelBase implements Serializable {

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // TODO: move to base class
    @PrePersist
    public void onPrePersist() {
        setCreatedAt(LocalDateTime.now());
        setUpdatedAt(LocalDateTime.now());
    }

    @PreUpdate
    public void onPreUpdate() {
        setUpdatedAt(LocalDateTime.now());
    }
}