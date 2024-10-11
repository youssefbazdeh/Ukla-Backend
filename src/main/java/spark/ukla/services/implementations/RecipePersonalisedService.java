package spark.ukla.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spark.ukla.DTO.RecipePersonnalisedDTO;
import spark.ukla.converters.RecipeConverter;
import spark.ukla.converters.RecipePersonnalisedConverter;
import spark.ukla.entities.*;
import spark.ukla.repositories.*;
import spark.ukla.services.interfaces.IRecipePersonnalised;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecipePersonalisedService implements IRecipePersonnalised {

    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    RecipePersonnalisedRepository repository;



    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    RecipeConverter recipeConverter;

    @Autowired
    MealRepository mealRepository;

    @Autowired
    RecipePersonnalisedConverter converter;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StepRepository stepRepository;
    @Autowired
    IngredientQuantityObjectRepository ingredientQuantityObjectRepository;

    @Autowired
    FileSystemRepository fileSystemRepository;


    @Override
    public String add(@Valid RecipePersonnalisedDTO personnalisedDTO, Image image) {

        RecipePersonnalised recipePersonnalised = converter.dtoToEntity(personnalisedDTO);
        boolean Exist = repository.existsByName(recipePersonnalised.getName());
        if (Exist)
            return "name recipe exist";
        else {
            Set<IngredientQuantityObject> ingredientslist = recipePersonnalised.getIngredientQuantityObjects();
            for (IngredientQuantityObject i : ingredientslist) {
                Ingredient ingredientExist = ingredientRepository.findByName(i.getIngredient().getName());
                if (ingredientExist != null) {
                    i.setIngredient(ingredientExist);
                } else {
                    Ingredient ing = ingredientRepository.save(i.getIngredient());
                    i.setIngredient(ing);
                }
                IngredientQuantityObject ingObject = ingredientQuantityObjectRepository.save(i) ;
                recipePersonnalised.getIngredientQuantityObjects().add(ingObject) ;
            }
            Set<Step> steps = recipePersonnalised.getSteps();
            for (Step s1 : steps) {
                    Step step2 = stepRepository.save(s1);
                    recipePersonnalised.getSteps().add(step2);
                }
            }
            recipePersonnalised.setImage(image);
            repository.save(recipePersonnalised);

        return "recipe saved";


    }

    @Override
    public RecipePersonnalisedDTO update(RecipePersonnalisedDTO personnalisedDTO) {

        RecipePersonnalised recipePersonnalised = converter.dtoToEntity(personnalisedDTO);


        Set<IngredientQuantityObject> ingredientslist = recipePersonnalised.getIngredientQuantityObjects();
        for (IngredientQuantityObject i : ingredientslist) {
            IngredientQuantityObject ingredientExist = ingredientQuantityObjectRepository.findById(i.getId()).orElse(null);
            if (ingredientExist != null) {
                recipePersonnalised.getIngredientQuantityObjects().add(ingredientExist);
            } else {
                IngredientQuantityObject ing = ingredientQuantityObjectRepository.save(i);
                recipePersonnalised.getIngredientQuantityObjects().add(ing);
            }
            IngredientQuantityObject ingObject = ingredientQuantityObjectRepository.save(i) ;
            recipePersonnalised.getIngredientQuantityObjects().add(ingObject) ;
        }
        Set<Step> steps = recipePersonnalised.getSteps();
        Set<Step> stepList = new HashSet<>();
        recipePersonnalised.setSteps(stepList);
        for (Step s1 : steps) {
            Step step1 = stepRepository.findById(s1.getId()).orElse(null);
            if (step1 != null) {
                recipePersonnalised.getSteps().add(step1);
            } else {
                Step step2 = stepRepository.save(s1);
                recipePersonnalised.getSteps().add(step2);
            }
        }
        RecipePersonnalised recipeSaved = repository.save(recipePersonnalised);
        return converter.entityToDTO(recipeSaved);
    }

    @Override
    public String deleteById(Long id) {
        RecipePersonnalised personnalised = repository.findById(id).orElse(null);
        if(personnalised!=null) {
            repository.deleteById(id);
            return "recipe Personalised has been deleted" ;
        }
        else
            return  "id not found";
    }

    @Override
    public List<RecipePersonnalisedDTO> retrieveByToAvoid(String toAvoid) {
        List<RecipePersonnalised> recipePersonnaliseds = repository.findByToAvoid(toAvoid);
        return converter.entitiesToDTOS(recipePersonnaliseds);
    }

    @Override
    public List<RecipePersonnalisedDTO> retrieveByToRecommend(String toRecommend) {
        List<RecipePersonnalised> recipePersonnaliseds = repository.findByToRecommend(toRecommend);
        return converter.entitiesToDTOS(recipePersonnaliseds);
    }

    @Override
    public List<RecipePersonnalisedDTO> retrieveByNbrCaloriesBetween(float calorieMin, float calorieMax) {
        List<RecipePersonnalised> recipePersonnaliseds = repository.findByNbrCaloriesBetween(calorieMin,calorieMax);
        return converter.entitiesToDTOS(recipePersonnaliseds);
    }

    @Override
    public void addFavoriteRecipePersonalisedToUser(String username, List<Long> idFavorisPerson) {
        User user = userRepository.findByUsername(username);
        Iterable<RecipePersonnalised> recipes = repository.findAllById(idFavorisPerson);
        for(RecipePersonnalised recipe:recipes){
            user.getRecipiesPersonnalised().add(recipe);
        }
    }

    @Override
    public void deleteFavoriteRecipePersonalisedFromUser(String username, List<Long> idFavorisPerson) {
        User user = userRepository.findByUsername(username);
        Iterable<RecipePersonnalised> recipes = repository.findAllById(idFavorisPerson);
        for(RecipePersonnalised recipe:recipes){
            user.getRecipiesPersonnalised().remove(recipe);
            repository.save(recipe);
        }
    }

    @Override
    public List<RecipePersonnalisedDTO> retrieveByPreparationTime(int startPreparationTime, int endPreparationTime) {
        List<RecipePersonnalised> recipePersonnalised = repository.findByPreparationTimeBetween(startPreparationTime, endPreparationTime);
    return converter.entitiesToDTOS(recipePersonnalised);
    }

    @Override
    public List<RecipePersonnalisedDTO> retrieveByCookingTime(int startCookingTime, int endCookingTime) {
        List<RecipePersonnalised> recipePersonnalised = repository.findByCookingTimeBetween(startCookingTime,endCookingTime);
        return converter.entitiesToDTOS(recipePersonnalised);
    }

    @Override
    public List<RecipePersonnalisedDTO> retrieveByPreparationAndCookingTime(int startPreparationTime, int endPreparationTime, int startCookingTime, int endCookingTime) {
        List<RecipePersonnalised> recipePersonnaliseds = repository.findByPreparationTimeBetweenAndCookingTimeBetween(startPreparationTime,endPreparationTime,startCookingTime,endCookingTime);
        return converter.entitiesToDTOS(recipePersonnaliseds);
    }

    @Override
    public String updateImage(Image image, Long id) {
        RecipePersonnalised recipe = repository.findById(id).orElse(null);
        if(recipe == null)
            return "recipe not found";
        recipe.setImage(image);
        repository.save(recipe);
        return "image updated";
    }

    @Override
    public List<RecipePersonnalised> searchRecipePersonalised(String query) {
        return repository.searchRecipePersonalised(query);
    }

    @Override
    public RecipePersonnalised retrieveById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<RecipePersonnalisedDTO> retrieveAll() {
        List<RecipePersonnalised> recipePersonnaliseds = repository.findAll();
        return converter.entitiesToDTOS(recipePersonnaliseds);
    }


}
