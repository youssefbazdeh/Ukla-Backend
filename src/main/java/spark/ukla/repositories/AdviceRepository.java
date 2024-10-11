package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Advice;
import spark.ukla.repositories.generic.GenericRepository;


import java.util.Optional;

@Repository
public interface AdviceRepository extends GenericRepository<Advice> {

    boolean existsByText(String text);

    Advice findByText(String text);

    Optional<Advice> findById(Long id);

    boolean existsById(Long id);
    @Modifying
    @Query("update Advice a set a.id= :id, a.text= :text  where a.id= :id")
    int update(@Param("id") Long id, @Param("text") String text);

    @Modifying
    @Query("delete from Advice a where a.id = ?1")
    void deleteAdviceById(@Param("id") Long id);
}