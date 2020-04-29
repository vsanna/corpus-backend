package dev.ishikawa.corpus.infrastructure.messaging;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import org.springframework.stereotype.Component;

// TODO: 本当はMessagingのIFを設けてそれをrepositoryにおき、それを介してこの実装クラスを利用する
@Component
public class InMemoryMessaging {

    static {
        topics = new HashMap<>() {{
            List.of(TopicName.values()).forEach(topicName -> {
                put(topicName, new LinkedList<>());
            });
        }};
    }

    public enum TopicName {
        CRAWLER,
        PARSER
    }

    static final Map<TopicName, Queue<String>> topics;

    private InMemoryMessaging() {
    }

    static public void publish(TopicName topicName, String message) {
        synchronized (topics.get(topicName)) {
            topics.get(topicName).add(message);
        }
    }

    static public Optional<String> consume(TopicName topicName) {
        synchronized (topics.get(topicName)) {
            return Optional.ofNullable(topics.get(topicName).poll());
        }
    }
}
