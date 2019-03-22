package autocheck.models;

import org.springframework.data.repository.CrudRepository;

public interface XmlTagContentRepository extends CrudRepository<XmlTagContent, Long> {
    Iterable<XmlTagContent> findByXmlIdOrderById(Long xml_id);
}
