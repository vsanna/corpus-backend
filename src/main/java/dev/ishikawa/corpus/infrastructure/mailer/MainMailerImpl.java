package dev.ishikawa.corpus.infrastructure.mailer;

import dev.ishikawa.corpus.configuration.property.ApplicationProperties;
import dev.ishikawa.corpus.domain.Ranking.Type;
import dev.ishikawa.corpus.util.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MainMailerImpl implements MainMailer {
    private final JavaMailSender javaMailSender;
    private final Message m;
    private final ApplicationProperties applicationProperties;

    public MainMailerImpl(
            @Autowired JavaMailSender javaMailSender,
            @Autowired Message m,
            @Autowired ApplicationProperties applicationProperties
    ) {
        this.javaMailSender = javaMailSender;
        this.m = m;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void sendAnalysisReport(String report, Type type) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(applicationProperties.getMailer().getAdminMails().getAdmin());
        message.setSubject(
                m.l("mailer.analysisReport.subject",
                        m.l(String.format("common.date.labels.%s",
                                type.toString().toLowerCase()))));
        message.setText(report);
        message.setFrom(applicationProperties.getMailer().getSender());
        javaMailSender.send(message);
    }
}
