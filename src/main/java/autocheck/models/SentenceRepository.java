package autocheck.models;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SentenceRepository extends CrudRepository<Sentence, Long> {
    @Query("select sen from Sentence sen where sen.doc_id = ?1")
    List<Sentence> findByDoc_id(Long doc_id);

    @Query("select sen from Sentence sen where sen.type = ?1 and sen.doc_id <> ?2")
    List<Sentence> findByTypeAndDoc_idIsNot(String type, Long doc_id);
}
