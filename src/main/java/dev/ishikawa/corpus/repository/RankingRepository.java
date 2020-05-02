package dev.ishikawa.corpus.repository;

import dev.ishikawa.corpus.domain.Ranking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankingRepository extends CrudRepository<Ranking, String> {

}