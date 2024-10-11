package spark.ukla.repositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.TranslatedIngredient;


@Repository
public interface TranslatedIngredientRepository extends CrudRepository<TranslatedIngredient, Long> {


}
