package autocheck.models;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, Long> {
//    @Query(value = "select * from document where status == ?1 and filetype == ?2", nativeQuery = true)
    Iterable<Document> findByStatusAndFiletype(Integer status, Integer filetype);
    Iterable<Document> findByFiletype(Integer filetype);
}
