package dev.ishikawa.corpus.configuration;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import net.rakugakibox.util.YamlResourceBundle;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageSourceConfiguration {
    @Bean
    public MessageSource messageSource() {
        YamlMessageSource messageSource = new YamlMessageSource();
        messageSource.setBasenames(
                "labels/messages"
//                "labels/messages_ja"
        );
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setFallbackToSystemLocale(true);
        return messageSource;
    }

    class YamlMessageSource extends ResourceBundleMessageSource {
        @Override
        protected ResourceBundle doGetBundle(String basename, Locale locale) throws MissingResourceException {
            return ResourceBundle.getBundle(basename, locale, YamlResourceBundle.Control.INSTANCE);
        }
    }
}