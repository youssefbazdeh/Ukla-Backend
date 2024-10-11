package spark.ukla.foodIntakeEstimation.EstimationRecipe;

import javassist.NotFoundException;

import java.util.List;

public interface IEstimationRecipeService {
    List<EstimationRecipe> getAll();
    EstimationRecipe add(EstimationRecipe estimationRecipe,Long estimationMeal);
    boolean delete(Long id);
    Boolean update(EstimationRecipe estimationRecipe) throws NotFoundException;

    Boolean addListQuantity(Long Id, List<Long> estimationIngredientQuantityIds);

    boolean deleteQuantity(Long Id, Long estimationIngredientQuantityId);
}
