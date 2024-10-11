package spark.ukla.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import spark.ukla.entities.IngredientQuantityObject;

public interface IngredientQuantityObjectRepository extends CrudRepository<IngredientQuantityObject, Long> {
IngredientQuantityObject findByQuantity(String quantity);

    @Override
    void delete(IngredientQuantityObject entity);
    @Modifying
    @Query(value = "DELETE siqo FROM step_ingredient_quantity_objects siqo WHERE siqo.ingredient_quantity_objects_id  = :ingredient_quantity_objects_id", nativeQuery = true)
    void removeStep_ingredient_quantity_objectsAssociation(@Param("ingredient_quantity_objects_id") Long ingredient_quantity_objects_id);
}
