package spark.ukla.services.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.groceyList.GroceryList;
import java.util.List;


@Repository
public interface GroceryListRepository extends JpaRepository<GroceryList,Long> {

    @Query("select g from GroceryList g,User u where g.user.username= :username")
    GroceryList getByUsername(@Param("username") String username);

    @Query("SELECT g FROM GroceryList g WHERE g.planOfWeekId = :planId")
    GroceryList findByPlanOfWeekId(@Param("planId") Long planId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM `grocery_list_grocery_days`  WHERE grocery_days_id in :ids",nativeQuery = true)
    void deletGroceryListGroceryDayById(@Param("ids") List<Long> ids);
}
