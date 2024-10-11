package spark.ukla.foodIntakeEstimation.EstimationIngredient;

import spark.ukla.entities.Image;

import java.util.List;

public interface IEstimationIngredientService {

    List<EstimationIngredient> SearchByName(String name);

    List<EstimationIngredient> getAll();
    Boolean add(EstimationIngredient estimationIngredient, Image image);
    boolean delete(Long id);
    Boolean update(EstimationIngredient estimationIngredient);


}

