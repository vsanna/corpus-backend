package dev.ishikawa.corpus.util;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

//Builderは内部でAllArgConstructorを作っている
@Component
public class Message {
    private final MessageSource messageSource;
    public Locale locale;

    Message(
            @Autowired MessageSource messageSource
    ) {
        this.messageSource = messageSource;
        this.locale = Locale.ENGLISH;
    }

    // TODO: userの状態に応じてlocaleを使い分けたい. request処理の最初の方にhookさせてlocaleを付け替える
    public Message setLocale(Locale loc) {
        Message msg = new Message(messageSource);
        msg.locale = loc;
        return msg;
    }

    public String l(String code, String... fills) {
        return messageSource.getMessage(code, fills, locale);
    }
}
