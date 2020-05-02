package dev.ishikawa.corpus.infrastructure.mailer;

import dev.ishikawa.corpus.domain.Ranking.Type;

public interface MainMailer {
    void sendAnalysisReport(String report, Type type);
}
