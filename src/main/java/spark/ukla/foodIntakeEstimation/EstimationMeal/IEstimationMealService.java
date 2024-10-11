package spark.ukla.foodIntakeEstimation.EstimationMeal;

import java.util.List;
import java.util.Optional;

public interface IEstimationMealService {

    List<EstimationMeal> getAll();

    List<EstimationMeal> getAllByUserId(String username);

    Boolean add(EstimationMeal estimationMeal, String username);
    boolean delete(Long id);
    Boolean updatename(Long id, String estimationMealName);
    Optional<EstimationMeal> getOne(Long id);
    boolean removeEstimationRecipe(Long idMeal, Long estimationRecipe);
}
