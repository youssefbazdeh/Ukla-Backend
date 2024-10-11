package spark.ukla.foodIntakeEstimation.EstimationMeal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.foodIntakeEstimation.EstimationRecipe.EstimationRecipe;

import java.util.List;

@Repository
public interface EstimationMealRepository  extends JpaRepository<EstimationMeal,Long> {
    @Modifying
    @Transactional
    @Query("Update EstimationMeal set name = :i where id = :id")
    void updateMealname(Long id,String i);

    List<EstimationMeal> findAllByUserId(Long userId);
}
