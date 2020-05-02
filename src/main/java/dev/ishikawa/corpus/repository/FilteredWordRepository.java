package dev.ishikawa.corpus.repository;

import dev.ishikawa.corpus.domain.FilteredWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FilteredWordRepository extends JpaRepository<FilteredWord, Integer>, JpaSpecificationExecutor<FilteredWord> {

}