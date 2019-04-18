package autocheck.models;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, Long> {
    //自定义查询语句
//    @Query(value = "select * from document where status == ?1 and filetype == ?2", nativeQuery = true)
    Iterable<Document> findByStatusAndFiletype(Integer status, Integer filetype);
    Iterable<Document> findByFiletype(Integer filetype);
    Iterable<Document> findByStatus(Integer status);
    Iterable<Document> findByFiletypeOrderByIdDesc(Integer filetype);

    @Query(value = "select * from document where status = 1 and (filetype = 1 or filetype = 2)", nativeQuery = true)
    Iterable<Document> findByStatusAndFiletypeOrFiletype();
}
