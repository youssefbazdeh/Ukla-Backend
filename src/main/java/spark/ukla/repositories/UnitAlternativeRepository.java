package spark.ukla.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.UnitAlternative;
@Repository
public interface UnitAlternativeRepository extends CrudRepository<UnitAlternative,Long> {

}
