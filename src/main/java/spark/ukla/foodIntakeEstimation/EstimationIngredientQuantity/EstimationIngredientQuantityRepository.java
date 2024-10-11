package spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface EstimationIngredientQuantityRepository extends CrudRepository<EstimationIngredientQuantity, Long> {
    @Transactional
    @Modifying
    @Query("update EstimationIngredientQuantity es set es.quantity= :quantity  where es.id= :id")
    int updateName(@Param("id") Long id, @Param("quantity") int quantity);

    List<EstimationIngredientQuantity> getAllByEstimationIngredient_Id(Long idEstimationIngredient);
}