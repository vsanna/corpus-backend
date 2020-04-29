package dev.ishikawa.corpus.service.parser;

import dev.ishikawa.corpus.domain.CrawlerArticle;
import dev.ishikawa.corpus.domain.Word;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.tokenize.Tokenizer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ParserWorker {

    private final Tokenizer tokenizer;
    private final POSTagger posTagger;
    private final DictionaryLemmatizer lemmatizer;

    public List<Word> run(CrawlerArticle article) {
        List<Word> words = new ArrayList<>();

        for (String sentence : article.getBody().split("\\.")) {
            log.info("sentence: {}", sentence);

            String[] tokens = tokenizer.tokenize(sentence);
            String[] tags = posTagger.tag(tokens);
            String[] lemmatize = lemmatizer.lemmatize(tokens, tags);

            for (int i = 0; i < tokens.length; i++) {
                words.add(
                    Word.builder()
                        .word(tokens[i])
                        .type(tags[i])
                        .originalForm(lemmatize[i])
                        .sentence(sentence).build()
                );
            }
        }

        return words;
    }
}
