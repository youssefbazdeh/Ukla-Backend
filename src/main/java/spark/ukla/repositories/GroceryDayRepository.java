package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.groceyList.GroceryDay;

import java.util.List;


@Repository
public interface GroceryDayRepository extends JpaRepository<GroceryDay,Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM grocery_day WHERE id IN (:ids)", nativeQuery = true)
    void deleteGroceryDaysByIds(@Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM `grocery_day_recipes` WHERE grocery_day_id IN (:ids)", nativeQuery = true)
    void deleteGroceryDayInAssociationGroceryDayRecipesByIds(@Param("ids") List<Long> ids);
    @Modifying
    @Query(value = "DELETE gdr FROM grocery_day_recipes gdr WHERE gdr.recipes_id = :recipesId", nativeQuery = true)
    void removeGrocery_day_recipesAssociation(@Param("recipesId") Long recipesId);

}
