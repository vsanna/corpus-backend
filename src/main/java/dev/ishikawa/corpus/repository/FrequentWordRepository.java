package dev.ishikawa.corpus.repository;

import dev.ishikawa.corpus.domain.FrequentWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FrequentWordRepository extends JpaRepository<FrequentWord, Integer>, JpaSpecificationExecutor<FrequentWord> {

}