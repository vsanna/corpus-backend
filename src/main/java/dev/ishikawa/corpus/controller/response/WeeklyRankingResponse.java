package dev.ishikawa.corpus.controller.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WeeklyRankingResponse {
    private final LocalDate from;
    private final LocalDate to;
    private final List<String> records;


    @Override
    public String toString() {
        return "WeeklyRankingResponse{" +
            "from=" + from +
            ", to=" + to +
            ", records=" + records +
            '}';
    }
}
