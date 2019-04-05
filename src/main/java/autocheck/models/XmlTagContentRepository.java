package autocheck.models;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.json.JsonObject;
import java.util.List;

public interface XmlTagContentRepository extends CrudRepository<XmlTagContent, Long> {
    Iterable<XmlTagContent> findByXmlIdOrderById(Long xml_id);

    Iterable<XmlTagContent> findByXmlId(Long xmlId);

}
