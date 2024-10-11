package spark.ukla.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Ingredient;

import java.util.List;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Long> {
	Ingredient findByName(String name);
	Boolean existsByName (String name ) ;
	Page<Ingredient> findAll(Pageable pageable);


	@Query(value= "select conversion_rate from unit_alternative ua join ingredient_unit_alternatives iua on ua.id=iua.unit_alternatives_id join ingredient i on i.id=iua.ingredient_id  where i.id =:id and ua.unit =:unit",nativeQuery = true )
	double getConversionRate(@Param("id") Long id,@Param("unit") int unit );


	List<Ingredient> findByNameContaining(String query) ;

	@Query("SELECT DISTINCT i FROM Ingredient i JOIN i.translatedIngredients t WHERE t.languageCode = :languageCode AND (t.name) LIKE (CONCAT('%', :queryPattern, '%'))")
	List<Ingredient> findIngredientsByTranslatedIngredients(@Param("languageCode") String languageCode, @Param("queryPattern") String queryPattern);

	@Query("SELECT DISTINCT i FROM Ingredient i JOIN i.translatedIngredients t WHERE (t.name) LIKE (CONCAT('%', :tname, '%'))")
	Ingredient findIngredientByTranslatedIngredient( @Param("tname") String tname);
}
