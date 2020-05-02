package dev.ishikawa.corpus.error;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException() {
    }

    public RecordNotFoundException(String message) {
        super(message);
    }

    public <T> RecordNotFoundException(Class<T> klass, int id) {
        super(String.format("%s [id=%s] was not found", klass.getName(), id));
    }
}
