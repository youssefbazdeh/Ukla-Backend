package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.Meal;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Long>{
    boolean existsById(Long id);

    @Transactional
    @Modifying
    @Query("update Meal m set  m.name= :name  where m.id= :id")
    int updateName(@Param("id") Long id, @Param("name") String name);

    @Modifying
    @Query(value = "DELETE mr FROM meal_recipes mr WHERE mr.recipes_id  = :recipesId", nativeQuery = true)
    void removeMeal_recipesAssociation(@Param("recipesId") Long recipesId);




    List<Meal> findAllByDayId(Long id);
}