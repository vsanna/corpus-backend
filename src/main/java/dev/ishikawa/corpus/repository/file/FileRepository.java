package dev.ishikawa.corpus.repository.file;

import java.util.Optional;

public interface FileRepository {

    Optional<byte[]> getObject(String key);

    void putObject(String key, byte[] data);
}
