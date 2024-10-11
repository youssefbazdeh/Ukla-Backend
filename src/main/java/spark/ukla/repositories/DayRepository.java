package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Day;

@Repository
public interface DayRepository extends JpaRepository<Day, Long> {
    boolean existsById(Long id);

    @Query("SELECT d.name FROM Day d WHERE d.id = :id")
    String findNameById(@Param("id") Long id) ;


}