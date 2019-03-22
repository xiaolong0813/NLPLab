package autocheck.models;

import org.springframework.data.repository.CrudRepository;

public interface XmlRepository extends CrudRepository<Xml, Long> {
//    中间多加了一个By，在All后面
    Iterable<Xml> findAllByOrderByIdDesc();
}
