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

        List<String> sentences = List.of(article.getBody().split("\\."));

        // NOTE: https://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
        String[] tokens = tokenizer.tokenize(article.getBody());
        String[] tags = posTagger.tag(tokens);
        String[] lemmatize = lemmatizer.lemmatize(tokens, tags);

        for (int i = 0; i < tokens.length; i++) {

            final String word = tokens[i].trim();
            final String tag = tags[i].trim();
            final String lemm = lemmatize[i].trim();

            // tagが不正なものは除外
            if (!tag.matches("[A-Z]+\\$?")) {
                continue;
            }
            // 2文字以上の単語に限定する
            if (!word.matches("[a-zA-Z0-9]{2,}")) {
                continue;
            }

            // 特定のtag typeを無視
            // CD: cardinal number, 基数
            // NNP: 固有名詞
            // NNPS: 固有名詞複数形
            if (List.of("CD", "NNP", "NNPS").contains(tag)) {
                continue;
            }

            log.debug("word: {}, tag: {}, lemmatize: {}", word, tag, lemm);

            words.add(
                    Word.builder()
                            .word(word)
                            .type(tag)
                            .originalForm(lemm)
                            .sentence(sentences.stream()
                                    .filter(sentence -> sentence.trim().toLowerCase().contains(word.toLowerCase()))
                                    .findFirst().orElse("dummy sentenec. this shold be handeld as an error"))
                            .build()
                    // TODO: .でsplitすると本来位置単語のものも分割され正しく評価できない
            );
        }

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
