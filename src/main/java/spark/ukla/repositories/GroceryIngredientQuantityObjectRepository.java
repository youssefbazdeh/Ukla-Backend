package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.groceyList.GroceryIngredientQuantityObject;

import java.util.List;

@Repository
public interface GroceryIngredientQuantityObjectRepository  extends JpaRepository<GroceryIngredientQuantityObject,Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM `grocery_recipe_grocery_ingredient_quantity_objects` WHERE grocery_ingredient_quantity_objects_id in :ids" ,nativeQuery = true)
    void deleteGroceryIngredientQuantityObjectsInAssociationTableByIds(@Param("ids")List<Long>ids);

    @Transactional
    @Modifying
    @Query("update GroceryIngredientQuantityObject g set g.purchased=true where g.id in :ids")
    void purchase(@Param("ids") List<Long> idsList);

    @Transactional
    @Modifying
    @Query("update GroceryIngredientQuantityObject g set g.purchased=false where g.id in :ids")
    void unpurchase(@Param("ids") List<Long> idsList);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM `grocery_ingredient_quantity_object` WHERE id IN (:ids)", nativeQuery = true)
    void deleteGroceryIngredientQuantityObjectsByIds(@Param("ids") List<Long> ids);
}
