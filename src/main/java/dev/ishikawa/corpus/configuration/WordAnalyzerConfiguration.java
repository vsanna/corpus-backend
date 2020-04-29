package dev.ishikawa.corpus.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
public class WordAnalyzerConfiguration {

    @Bean
    public Tokenizer tokenizer() throws IOException {
        File file = ResourceUtils.getFile("classpath:en-token.bin");
        InputStream modelIn = new FileInputStream(file);
        TokenizerModel model = new TokenizerModel(modelIn);
        return new TokenizerME(model);
    }

    @Bean
    public POSTagger posTagger() throws IOException {
        File file = ResourceUtils.getFile("classpath:en-pos-maxent.bin");
        InputStream posModelIn = new FileInputStream(file);
        POSModel posModel = new POSModel(posModelIn);
        return new POSTaggerME(posModel);
    }

    @Bean
    public DictionaryLemmatizer dictionaryLemmatizer() throws IOException {
        File file = ResourceUtils.getFile("classpath:en-lemmatizer.txt");
        InputStream dictLemmatizer = new FileInputStream(file);
        return new DictionaryLemmatizer(dictLemmatizer);
    }
}