package spark.ukla.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import spark.ukla.entities.Image;
import spark.ukla.entities.Ingredient;

import java.io.IOException;
import java.util.List;

public interface IIngredientsService {

    List<Ingredient> importIngredientsFromExcel(MultipartFile file) throws IOException;
    Boolean add(Ingredient ingredient, Image image) ;
    Boolean nameExists(String ingredientName) ;
    List<String> getAllIngredientName();
    List<Ingredient> searchIngredient(String query);
}
