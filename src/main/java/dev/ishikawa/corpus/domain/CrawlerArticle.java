package dev.ishikawa.corpus.domain;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "crawler_articles")
@Data
public class CrawlerArticle extends ModelBase {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "body")
    private String body;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;

    @Column(name = "html_file_name")
    private String htmlFileName;

    @Column(name = "wordlist_file_name")
    private String wordlistFileName;

    @Default
    @Column(name = "parse_status", nullable = false)
    private Integer parseStatusCode = 0;

    // TODO: このmappingを省略したい
    public ParseStatus getParseStatus() {
        return List.of(ParseStatus.values()).stream()
                .filter(status -> status.getCode() == parseStatusCode)
                .findFirst()
                .orElseThrow(() -> {
                    // TODO: appを壊してほしい
                    throw new RuntimeException("invalid status");
                });
    }

    @Default
    @Column(name = "media_type", nullable = false)
    private Integer mediaTypeCode = 0;


    public Medium getMediaName() {
        return List.of(Medium.values()).stream()
                .filter(status -> status.getCode() == mediaTypeCode)
                .findFirst()
                .orElseThrow(() -> {
                    // TODO: appを壊してほしい
                    throw new RuntimeException("invalid status");
                });
    }
}