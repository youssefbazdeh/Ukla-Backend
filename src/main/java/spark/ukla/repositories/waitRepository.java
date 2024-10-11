package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.waitList;
@Repository
public interface waitRepository extends JpaRepository<waitList, Long> {
    boolean existsByEmail(String email);

}
