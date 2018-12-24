package autocheck.models;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SentenceRepository extends CrudRepository<Sentence, Long> {
    @Query("select sen from Sentence sen where sen.doc_id = ?1")
    List<Sentence> findByDoc_id(Long doc_id);

    @Query("select sen from Sentence sen where sen.doc_id <> ?1")
    List<Sentence> findByDoc_idIsNot(Long doc_id);
}
