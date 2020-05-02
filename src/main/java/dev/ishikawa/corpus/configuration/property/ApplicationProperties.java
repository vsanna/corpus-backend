package dev.ishikawa.corpus.configuration.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {
    private String name;
    private Mailer mailer;

    @Data
    static public class Mailer {
        private String sender;
        private AdminMails adminMails;

        @Data
        static public class AdminMails {
            private String admin;
        }
    }
}
