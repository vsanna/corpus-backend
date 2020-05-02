package dev.ishikawa.corpus.controller.analyzer;

import dev.ishikawa.corpus.controller.request.CreateFilteredWordRequest;
import dev.ishikawa.corpus.domain.FilteredWord;
import dev.ishikawa.corpus.service.analyzer.AnalyzerService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("FilteredWordsController")
@AllArgsConstructor
@RequestMapping("/filtered_words")
public class FilteredWordsController {
    final private AnalyzerService analyzerService;

    @GetMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<FilteredWord>> index() {
        return ResponseEntity.ok(analyzerService.getAllFilteredWords());
    }

    @GetMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FilteredWord> show(@PathVariable("id") int id) {
        return ResponseEntity.ok(analyzerService.getFilteredWord(id));
    }

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FilteredWord> create(@Validated @RequestBody CreateFilteredWordRequest request) {
        FilteredWord filteredWord = analyzerService.createFilteredWord(request.getWord());
        return ResponseEntity.ok(filteredWord);
    }

    @DeleteMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        analyzerService.destroyFilteredWord(id);
        return ResponseEntity.noContent().build();
    }
}
