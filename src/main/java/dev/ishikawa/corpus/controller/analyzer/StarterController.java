package dev.ishikawa.corpus.controller.analyzer;

import dev.ishikawa.corpus.service.analyzer.AnalyzerService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("AnalyzerStarter")
@AllArgsConstructor
@RequestMapping("/analyzer")
public class StarterController {
    final private AnalyzerService analyzerService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> run() {
        analyzerService.startAnalysisForDaily();
//        analyzerService.startAnalysisForWeekly();
//        analyzerService.startAnalysisForMonthly();
        return ResponseEntity.ok("OK");
    }
}
