package dev.ishikawa.corpus.error;

public class InvalidRecord extends RuntimeException {
    public InvalidRecord() {
    }

    public InvalidRecord(String message) {
        super(message);
    }

    // TODO: invalidな理由をセットする
    public <T> InvalidRecord(Class<T> klass, String msg) {
        super(String.format("%s was invalid, err: %s", klass.getName(), msg));
    }
}
