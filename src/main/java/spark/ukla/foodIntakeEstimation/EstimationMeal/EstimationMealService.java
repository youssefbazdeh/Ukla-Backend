package spark.ukla.foodIntakeEstimation.EstimationMeal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.User;
import spark.ukla.foodIntakeEstimation.EstimationRecipe.EstimationRecipe;
import spark.ukla.foodIntakeEstimation.EstimationRecipe.EstimationRecipeRepository;
import spark.ukla.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EstimationMealService implements IEstimationMealService{

    private final EstimationMealRepository estimationMealRepository;

    private final UserRepository userRepository;
    private final EstimationRecipeRepository estimationRecipeRepository;

    public EstimationMealService(EstimationMealRepository estimationMealRepository,
                                 UserRepository userRepository, EstimationRecipeRepository estimationRecipeRepository) {
        this.estimationMealRepository = estimationMealRepository;

        this.userRepository = userRepository;
        this.estimationRecipeRepository = estimationRecipeRepository;
    }

    @Override
    public List<EstimationMeal> getAll() {
        return estimationMealRepository.findAll();
    }

    @Override
    public List<EstimationMeal> getAllByUserId(String username) {
        Long userId = userRepository.findByUsername(username).getId();
        return estimationMealRepository.findAllByUserId(userId);
    }
    @Override

    public Boolean add(EstimationMeal estimationMeal, String username) {
        User user = userRepository.findByUsername(username);
        estimationMeal.setUser(user);
        estimationMealRepository.save(estimationMeal);
        return true;
    }

    @Override
    public boolean delete(Long id) {
        estimationMealRepository.deleteById(id);
        return true;
    }

    @Override
    public Boolean updatename(Long id, String estimationMealName) {
        boolean status = false;
        Optional<EstimationMeal> estimationMeal=estimationMealRepository.findById(id);
        if(estimationMeal.isPresent()){
            estimationMealRepository.updateMealname(id,estimationMealName);
            status= true;
        }
        return status;
    }
    @Override
    public Optional<EstimationMeal> getOne(Long id) {
        return estimationMealRepository.findById(id);
    }

    @Transactional
    @Override
    public boolean removeEstimationRecipe(Long idMeal, Long idEstimationRecipe) {
        EstimationMeal estimationMeal = estimationMealRepository.findById(idMeal).orElse(null);

        if (estimationMeal != null) {
            List<EstimationRecipe> estimationRecipes = estimationMeal.getEstimationRecipes();

            estimationRecipeRepository.findById(idEstimationRecipe).ifPresent(estimationRecipe -> {
                estimationRecipe.setEstimationMeal(null);
                estimationRecipes.remove(estimationRecipe);
                estimationRecipeRepository.save(estimationRecipe);
            });

            estimationMeal.setEstimationRecipes(estimationRecipes);
            estimationMealRepository.save(estimationMeal);

            return true;
        }

        return false;
    }
    }

