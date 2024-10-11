package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.groceyList.GroceryRecipe;

import java.util.List;

@Repository
public interface GroceryRecipeRepository extends JpaRepository<GroceryRecipe,Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM grocery_recipe WHERE id IN (:ids)", nativeQuery = true)
    void deleteGroceryRecipesByIds(@Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM grocery_day_recipes WHERE recipes_id IN (:ids)", nativeQuery = true)
    void deleteGroceryRecipesInAssociationTableByIds(@Param("ids") List<Long> ids);

    @Query("SELECT g from GroceryRecipe g where g.recipe.id = :id")
    List<GroceryRecipe> findGroceryRecipeByRecipe_Id(@Param("id") Long id);
}
