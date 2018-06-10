package autocheck.models;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ParameterRepository extends CrudRepository<Parameter, Long> {
    List<Parameter> findByName(String paraName);
}
