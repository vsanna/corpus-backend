package dev.ishikawa.corpus.service.parser;

import dev.ishikawa.corpus.domain.CrawlerArticle;
import dev.ishikawa.corpus.domain.Word;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging;
import dev.ishikawa.corpus.infrastructure.messaging.InMemoryMessaging.TopicName;
import dev.ishikawa.corpus.infrastructure.messaging.message.ParserEvent;
import dev.ishikawa.corpus.repository.CrawlerArticlesRepository;
import dev.ishikawa.corpus.repository.file.FileRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.tokenize.Tokenizer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class ParserWorker {

    private final Tokenizer tokenizer;
    private final POSTagger posTagger;
    private final DictionaryLemmatizer lemmatizer;
    private final FileRepository fileRepository;
    private final CrawlerArticlesRepository crawlerArticlesRepository;

    public void run(CrawlerArticle article) {
        List<Word> words = new ArrayList<>();

        // NOTE: https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
        List.of(article.getBody().split("\\. ")).stream().forEach(sentence -> {
            String[] tokens = tokenizer.tokenize(sentence);
            String[] tags = posTagger.tag(tokens);
            String[] lemmatize = lemmatizer.lemmatize(tokens, tags);

            for (int i = 0; i < tokens.length; i++) {
                if (!tags[i].matches("[A-Z]+\\$?")) {
                    continue;
                }

                words.add(
                        Word.builder()
                                .word(tokens[i])
                                .type(tags[i])
                                .originalForm(lemmatize[i])
                                .sentence(sentence).build()
                );
            }
        });

        String wordlistFilename = upload(article, words);
        article.setWordlistFileName(wordlistFilename);
        saveAndPublish(article);
    }


    private String upload(CrawlerArticle article, List<Word> words) {
        String wordListBody = words.stream().map(Word::toRow).collect(Collectors.joining("\n"));
        // TODO: 抽象化
        DateTimeFormatter yyyy_mm_dd_hh_mm_ss = DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss");
        String currentAt = LocalDateTime.now().format(yyyy_mm_dd_hh_mm_ss);
        String filename = String.format("%s_%s.tsv", article.getMediaName(), currentAt);
        fileRepository.putObject(filename, wordListBody.getBytes());
        return filename;
    }

    @Transactional
    void saveAndPublish(CrawlerArticle article) {
        // TODO: validationをどうすればいいのか
        crawlerArticlesRepository.saveAndFlush(article);
        InMemoryMessaging.publish(
                TopicName.PARSER,
                ParserEvent.parsedContent(article.getId()).toMessage());
    }
}
