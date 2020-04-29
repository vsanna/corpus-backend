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
public class ParserEvent {

    static final private ObjectMapper objectMapper = new ObjectMapper();

    public enum Type {
        PARSED_CONTENT,
        FAILED_PARSING_CONTENT
    }

    private Type eventType;
    private Map<String, String> payload;

    public String toMessage() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("cannot convert {} to message. object: {}, err: {}", this.getClass(), this,
                e);
            throw new RuntimeException("invalid event format");
        }
    }

    // TODO: 型安全にしたい
    static public ParserEvent parsedContent(int articleId) {
        return ParserEvent.builder()
            .eventType(Type.PARSED_CONTENT)
            .payload(new HashMap<>() {{
                put("articleId", String.valueOf(articleId));
            }}).build();
    }

    // TODO: 型安全にしたい
    static public ParserEvent failedParsingContent(MediumName mediumName, int articleId) {
        return ParserEvent.builder()
            .eventType(Type.FAILED_PARSING_CONTENT)
            .payload(new HashMap<>() {{
                put("articleId", String.valueOf(articleId));
            }}).build();
    }

    @Override
    public String toString() {
        // TODO: MapのtoString
        return "ParserEvent{" +
            "type=" + eventType +
            ", payload=" + payload +
            '}';
    }
}
