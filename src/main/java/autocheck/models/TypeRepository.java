package autocheck.models;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TypeRepository extends CrudRepository<Type, Long> {
    List<Type> findByName(String typeName);
}