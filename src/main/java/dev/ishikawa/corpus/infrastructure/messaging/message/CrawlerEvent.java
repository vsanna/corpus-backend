package dev.ishikawa.corpus.infrastructure.messaging.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.ishikawa.corpus.domain.Medium.MediumName;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CrawlerEvent {

    static final private ObjectMapper objectMapper = new ObjectMapper();

    public enum Type {
        FETCHED_URL,
        FETCHED_CONTENT,
        FAILED_FETCHING_CONTENT,
    }

    private Type eventType;
    private MediumName mediumName;
    private Map<String, String> payload;

    public String toMessage() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("cannot convert CrawlerEvent to message. object: {}, err: {}", this, e);
            throw new RuntimeException("invalid event format");
        }
    }

    // TODO: 型安全にしたい
    static public CrawlerEvent fetchedUrl(MediumName mediumName, String url) {
        return CrawlerEvent.builder()
            .mediumName(mediumName)
            .eventType(Type.FETCHED_URL)
            .payload(new HashMap<>() {{
                put("url", url);
            }}).build();
    }

    // TODO: 型安全にしたい
    static public CrawlerEvent fetchedContent(MediumName mediumName, int articleId) {
        return CrawlerEvent.builder()
            .mediumName(mediumName)
            .eventType(Type.FETCHED_CONTENT)
            .payload(new HashMap<>() {{
                put("articleId", String.valueOf(articleId));
            }}).build();
    }

    @Override
    public String toString() {
        // TODO: MapのtoString
        return "CrawlerEvent{" +
            "type=" + eventType +
            ", payload=" + payload +
            '}';
    }
}
