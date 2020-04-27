package dev.ishikawa.corpus.controller.ranking;

import dev.ishikawa.corpus.controller.response.WeeklyRankingResponse;
import java.time.LocalDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/ranking/weekly")
public class WeeklyController {
    @GetMapping
    public ResponseEntity<WeeklyRankingResponse> show(
        @RequestParam String date
    ) {
        // TODO: get ranking from date
        return ResponseEntity.ok(WeeklyRankingResponse.builder()
            .from(LocalDate.now().minusDays(10))
            .to(LocalDate.now().minusDays(3))
            .build());
    }
}
