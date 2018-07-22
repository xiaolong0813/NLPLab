package autocheck.models;

import org.springframework.data.repository.CrudRepository;

public interface DeviationRepository extends CrudRepository<Deviation, Long> {
    Iterable<Deviation>findByType(String type);
}
