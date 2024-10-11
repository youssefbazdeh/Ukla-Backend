package spark.ukla.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.PlanOfWeek;
import spark.ukla.repositories.projection.PlanOfWeekProjection;
import java.util.Optional;

@Repository
public interface PlanOfWeekRepository extends JpaRepository<PlanOfWeek, Long> {
    boolean existsByName(String name);

    PlanOfWeek findByName(String name);

    Optional<PlanOfWeek> findById(Long id);


    void deleteById(Long id);

    @Modifying
    @Query("update PlanOfWeek  i set  i.name= :name  where i.id= :id")
    void update(@Param("id") Long id, @Param("name") String name);

    @Modifying
    @Query("update PlanOfWeek  i set i.followed=:value  where i.id= :id")
    void updateFollowed(@Param("id") Long id,@Param("value") boolean value);


    @Modifying
    @Query("update PlanOfWeek  i set i.name=:value  where i.id= :id")
    void renamePLan(@Param("id") Long id,@Param("value") String value);


    Page<PlanOfWeekProjection> findAllByUser_Username(String username, Pageable pageable);
}