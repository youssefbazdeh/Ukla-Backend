package spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity;

import javassist.NotFoundException;
import spark.ukla.entities.Image;

import java.util.List;

public interface IEstimationIngredientQuantityService {
    List<EstimationIngredientQuantity> getAll( Long idEstimationIngredient);
    Boolean add(Long idEstimationIngredient,EstimationIngredientQuantity estimationIngredientQuantity, Image image);
    boolean delete(Long id);
    Boolean update(EstimationIngredientQuantity estimationIngredientQuantity) throws NotFoundException;

}
