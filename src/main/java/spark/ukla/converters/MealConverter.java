package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.MealDTO;
import spark.ukla.entities.Meal;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MealConverter {
    private final RecipeConverter recipeConverter;
    public MealConverter(RecipeConverter recipeConverter){
        this.recipeConverter = recipeConverter;
    }

    public MealDTO entityToDTO(Meal meal){
        MealDTO mealDTO = new MealDTO();
        mealDTO.setId(meal.getId());
        mealDTO.setName(meal.getName());
        mealDTO.setRecipes(recipeConverter.entitiesToRecipeCardDTO(meal.getRecipes()));
        return mealDTO;
    }

    public List<MealDTO> entitiesToDTO(List<Meal> meals){
        return meals.stream().map(x -> entityToDTO(x)).collect(Collectors.toList());
    }
}
