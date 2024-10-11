package spark.ukla.repositories;

import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Allergy;
import spark.ukla.entities.User;

import java.util.List;

@Repository
public interface AllergiesRepository extends JpaRepository<Allergy, Long> {
    @Query("SELECT DISTINCT a FROM Allergy a JOIN a.users u WHERE u.id = :userId")
    List<Allergy> findAllergiesByUserId(@Param("userId") Long userId);
    boolean existsByName(String name);

}