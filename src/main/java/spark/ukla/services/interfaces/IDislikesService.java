package spark.ukla.services.interfaces;

import spark.ukla.entities.Ingredient;

import java.util.List;

public interface IDislikesService {
    boolean addDislikedIngredient(String username, Long ingredientId);
    boolean addDislikedIngredients(String username, List<Long> ingredientIds);
    boolean removeDislikedIngredient(String username, Long ingredientId);
    List<Ingredient> getAllDislikedIngredients(String username);
}
