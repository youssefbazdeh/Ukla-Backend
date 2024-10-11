package spark.ukla.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Recipe;
import spark.ukla.entities.Tag;

import org.springframework.data.domain.Pageable;
import spark.ukla.entities.enums.Status;
import spark.ukla.repositories.projection.ViewRecipeProjection;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import java.util.List;
import java.util.Set;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	boolean existsByName(String name);
	boolean existsById(Long id);

	long countByCreatorUsername(String username);
	ViewRecipeProjection findByName(String name);

	ViewRecipeProjection findRecipeById(Long id);
	List<Recipe> findByTagsIn(Set<Tag> tags);

	List<Recipe> findRecipesByStatus(Status status);
	List<Recipe> findByCreator_Id(Long id);

	@Query("SELECT r FROM Recipe r WHERE r.creator.id = :creatorId AND (r.status = :accepted OR r.status = :verified)")
	Page<Recipe> findRecipesByCreatorAndEitherStatus(@Param("creatorId") Long creatorId,
													 @Param("accepted") Status accepted,
													 @Param("verified") Status verified,
													 Pageable pageable);
//	 @Query(value = "select Recipe.id from Recipe join IngredientQuantityObject on Recipe.id=IngredientQuantityObject.recipe.id join Ingredient on  IngredientQuantityObject.id= Ingredient.id where Ingredient in  ?1")
//	List<Recipe> findByIngredient(Set<Ingredient> ingredients) ;

// this works in sql we can select the recipe id or name or the whole thing
//	SELECT * FROM `recipe` JOIN ingredient_quantity_object ON recipe.id=ingredient_quantity_object.recipe_id JOIN ingredient on ingredient.id=ingredient_quantity_object.ingredient_id ;

//	  List<Recipe> findByIngredientQuantityObjects (Set<IngredientQuantityObject> );

//	List<Recipe>  findByIngredientQuantityObjectsContaining (Set<IngredientQuantityObject> ingredients);

	List<Recipe> findByPreparationTimeBetweenAndCookingTimeBetween(int startPreparationTime, int endPreparationTimen,
			int startCookingTime, int endCookingTime);



	List<Recipe> findByCaloriesBetween(float calorieMin, float calorieMax);

	@Query( value = "select * from user_favoris uf join recipe r on r.id = uf.favoris_id left join user on uf.user_id = user.id where user.username like %:username ", nativeQuery = true)
	Page<Recipe> getFavorite(@Param("username") String username, Pageable pageable);

//	Recipe findByIngredientsTypeAndCookingTime(String type,int cookingTime );

	@Query( "SELECT r FROM Recipe r WHERE" +
			" r.name LIKE CONCAT('%', :query, '%')" )
	List<Recipe> searchRecipe(String query);

	@Query(value="SELECT * FROM Recipe r  ORDER BY RAND() LIMIT 3; "
			, nativeQuery = true)
	List<Recipe> randomRecipe();

	@Query( value = "select * from user_favoris uf join recipe r on r.id = uf.favoris_id left join user on uf.user_id = user.id where user.username like %:username  and r.name LIKE CONCAT('%', :query, '%') " , nativeQuery = true)
	List<Recipe> searchRecipefromfavorites(String query , @Param("username") String username);

	List<Recipe> getAllByTagsTitle( String tags_title);

	Page<Recipe> findAll(Pageable pageable);

	@Query("SELECT r FROM Recipe r WHERE r.status IN (:statuses)")
	Page<Recipe> findAllVerifiedAndAcceptedRecipes(@Param("statuses") List<Status> statuses, Pageable pageable);

	List<Recipe> findAll(Specification<Recipe> spec, Pageable pageable);

	@Modifying
	@Query(value = "DELETE uf FROM user_favoris uf WHERE uf.favoris_id  = :favoris_id", nativeQuery = true)
	void removeUser_favorisAssociation(@Param("favoris_id") Long favoris_id);

	default List<Recipe> findByTimeAndTags(int time, Set<Tag> tags) {
		Specification<Recipe> spec = Specification.where(null);

		if (time != 0) {
			spec = spec.and(timeCheck(time));
		}

		if (!tags.isEmpty()) {
			spec = spec.and(tagsIn(tags));
		}
		return findAll(spec, Pageable.unpaged());
	}

	static Specification<Recipe> timeCheck(int time) {
		return (root, query, criteriaBuilder) -> {
			Expression<Integer> totalTime = criteriaBuilder.sum(
					root.get("cookingTime").as(Integer.class),
					root.get("preparationTime").as(Integer.class)
			);

			return criteriaBuilder.lessThanOrEqualTo(totalTime, time);
		};
	}

	static Specification<Recipe> tagsIn(Set<Tag> tags) {
		return (root, query, criteriaBuilder) -> {
			Join<Recipe, Tag> tagsJoin = root.join("tags");
			return tagsJoin.in(tags);
		};
	}
}
