package spark.ukla.foodIntakeEstimation.EstimationIngredient;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.enums.Unit;

import java.util.List;

public interface EstimationIngredientRepository extends CrudRepository<EstimationIngredient, Long> {
    @Transactional
    @Modifying
    @Query("update EstimationIngredient es set  es.name= :name, es.unit= :unit  where es.id= :id")
    int update(@Param("id") Long id, @Param("name") String name, @Param("unit") Unit unit);

    EstimationIngredient findByName(String name);

    Boolean existsByName(String name);

    @Query( value = "select ei from EstimationIngredient ei where ei.name LIKE CONCAT('%', :name, '%') ")
    List<EstimationIngredient> searchEstimationIngredientByName(String name);

}