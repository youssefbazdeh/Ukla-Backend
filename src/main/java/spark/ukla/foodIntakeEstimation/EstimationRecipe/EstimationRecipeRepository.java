package spark.ukla.foodIntakeEstimation.EstimationRecipe;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EstimationRecipeRepository extends CrudRepository<EstimationRecipe, Long> {
    @Transactional
    @Modifying
    @Query("update EstimationRecipe er set  er.name= :name, er.frequency= :frequency  where er.id= :id")
    void update(@Param("id") Long id, @Param("name") String name, @Param("frequency") int frequency);

}