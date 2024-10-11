package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Recipe;
import spark.ukla.entities.RecipePersonnalised;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipePersonnalisedRepository extends JpaRepository<RecipePersonnalised,Long>{

    boolean existsByName(String name);

    Optional<RecipePersonnalised> findById(Long id);

    void deleteById(Long id);

    List<RecipePersonnalised> findByToAvoid(String toAvoid);

    List<RecipePersonnalised> findByToRecommend(String toRecommend);

    List<RecipePersonnalised> findByNbrCaloriesBetween(float calorieMin, float calorieMax);


    	List<RecipePersonnalised> findByPreparationTimeBetween(int startPreparationTime, int endPreparationTime);

	List<RecipePersonnalised> findByCookingTimeBetween(int startCookingTime, int endCookingTime);

	List<RecipePersonnalised> findByPreparationTimeBetweenAndCookingTimeBetween(int startPreparationTime, int endPreparationTimen,
			int startCookingTime, int endCookingTime);

    @Query( value = "select * from user_recipies_personnalised uf join recipe_personnalised r on r.id = uf.recipies_personnalised_id left join user on uf.user_id = user.id where user.username like %:username ", nativeQuery = true)
    List<RecipePersonnalised> getFavorite(@Param("username") String username);


    @Query( "SELECT r FROM RecipePersonnalised r WHERE" +
            " r.name LIKE CONCAT('%', :query, '%')" )
    List<RecipePersonnalised> searchRecipePersonalised(String query);
}