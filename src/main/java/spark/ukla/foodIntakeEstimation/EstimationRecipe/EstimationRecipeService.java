package spark.ukla.foodIntakeEstimation.EstimationRecipe;

import javassist.NotFoundException;
import org.springframework.stereotype.Service;
import spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity.EstimationIngredientQuantity;
import spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity.EstimationIngredientQuantityRepository;
import spark.ukla.foodIntakeEstimation.EstimationMeal.EstimationMeal;
import spark.ukla.foodIntakeEstimation.EstimationMeal.EstimationMealRepository;

import java.util.*;

@Service
public class EstimationRecipeService implements IEstimationRecipeService{
    private final EstimationRecipeRepository estimationRecipeRepository;
    private final EstimationIngredientQuantityRepository estimationIngredientQuantityRepository;
    private final EstimationMealRepository estimationMealRepository;

    public EstimationRecipeService(EstimationRecipeRepository estimationRecipeRepository, EstimationIngredientQuantityRepository estimationIngredientQuantityRepository, EstimationMealRepository estimationMealRepository) {
        this.estimationRecipeRepository = estimationRecipeRepository;
        this.estimationIngredientQuantityRepository = estimationIngredientQuantityRepository;
        this.estimationMealRepository = estimationMealRepository;
    }

    @Override
    public List<EstimationRecipe> getAll() {
        return (List<EstimationRecipe>) estimationRecipeRepository.findAll();
    }

    @Override
    public EstimationRecipe add(EstimationRecipe estimationRecipe, Long idEstimationMeal) {
        EstimationMeal estimationMeal = estimationMealRepository.findById(idEstimationMeal).get();
        estimationRecipe.setEstimationMeal(estimationMeal);
        return estimationRecipeRepository.save(estimationRecipe);
    }

    @Override
    public boolean delete(Long id) {
        estimationRecipeRepository.deleteById(id);
        return true;
    }
    @Override
    public Boolean update(EstimationRecipe estimationRecipe) throws NotFoundException {

        if(estimationRecipe == null || estimationRecipe.getId() == null){
            throw new NotFoundException("Ingredient or ID must not be null! ");
        }
        Optional<EstimationRecipe> OptionalEstimationRecipe = estimationRecipeRepository.findById(estimationRecipe.getId());
        if(!OptionalEstimationRecipe.isPresent()){
            throw new NotFoundException("estimationRecipe with ID: " + estimationRecipe.getId() + " does not exist. ");
        }
        String Name = estimationRecipe.getName();
        int Frequency=estimationRecipe.getFrequency();
        estimationRecipeRepository.update(estimationRecipe.getId(), Name, Frequency);
        return true ;
    }
    @Override
    public Boolean addListQuantity(Long Id, List<Long> estimationIngredientQuantityIds) {
        EstimationRecipe estimationRecipe = estimationRecipeRepository.findById(Id).get();
        List<EstimationIngredientQuantity> estimationIngredientQuantity = estimationRecipe.getEstimationIngredientQuantities();
        for (Long estimationIngredientQuantityId : estimationIngredientQuantityIds){
            EstimationIngredientQuantity estimationIngredientQuantity1 = estimationIngredientQuantityRepository.findById(estimationIngredientQuantityId).get();
            estimationIngredientQuantity.add(estimationIngredientQuantity1);}
        estimationRecipe.setEstimationIngredientQuantities(estimationIngredientQuantity);
        estimationRecipeRepository.save(estimationRecipe);
        return true;
    }

    @Override
    public boolean deleteQuantity(Long estimationRecipeId, Long estimationIngredientQuantityId) {
        EstimationIngredientQuantity estimationIngredientQuantity = estimationIngredientQuantityRepository.findById(estimationIngredientQuantityId).get();
        EstimationRecipe estimationRecipe=estimationRecipeRepository.findById(estimationRecipeId).get();
        List<EstimationIngredientQuantity> estimationIngredientQuantityIds = estimationRecipe.getEstimationIngredientQuantities();
        estimationIngredientQuantityIds.remove(estimationIngredientQuantity);
        estimationRecipe.setEstimationIngredientQuantities(estimationIngredientQuantityIds);
        estimationRecipeRepository.save(estimationRecipe);
        return true;
    }
}
