package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.RecipePersonnalisedDTO;
import spark.ukla.entities.RecipePersonnalised;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RecipePersonnalisedConverter {

    public RecipePersonnalisedDTO entityToDTO(RecipePersonnalised recipe) {
        RecipePersonnalisedDTO dto = new RecipePersonnalisedDTO();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setDescription(recipe.getDescription());
        dto.setPreparationTime(recipe.getPreparationTime());
        dto.setCookingTime(recipe.getCookingTime());
        dto.setType(recipe.getType());
        dto.setRecipeSeparations(recipe.getRecipeSeparations());
        dto.setToAvoid(recipe.getToAvoid());
        dto.setToRecommend(recipe.getToRecommend());
        dto.setNbrCalories(recipe.getNbrCalories());

        dto.setIngredientQuantityObjects(recipe.getIngredientQuantityObjects());
        dto.setSteps(recipe.getSteps());
        dto.setLocation(recipe.getLocation());
        return dto;
    }

    public List<RecipePersonnalisedDTO> entitiesToDTOS(List<RecipePersonnalised> recipes) {
        return recipes.stream().map(x -> entityToDTO(x)).collect(Collectors.toList());
    }

    public List<RecipePersonnalisedDTO> entitiesToDTOS(Set<RecipePersonnalised> recipePersonnaliseds) {
        return recipePersonnaliseds.stream().map(x -> entityToDTO(x)).collect(Collectors.toList());
    }

    public RecipePersonnalised dtoToEntity(RecipePersonnalisedDTO recipeDTO) {
        RecipePersonnalised recipe = new RecipePersonnalised();
        recipe.setId(recipeDTO.getId());
        recipe.setName(recipeDTO.getName());
        recipe.setDescription(recipeDTO.getDescription());
        recipe.setPreparationTime(recipeDTO.getPreparationTime());
        recipe.setCookingTime(recipeDTO.getCookingTime());
        recipe.setType(recipeDTO.getType());
        recipe.setRecipeSeparations(recipeDTO.getRecipeSeparations());
        recipe.setToAvoid(recipeDTO.getToAvoid());
        recipe.setToRecommend(recipeDTO.getToRecommend());
        recipe.setNbrCalories(recipeDTO.getNbrCalories());
        recipe.setIngredientQuantityObjects(recipeDTO.getIngredientQuantityObjects());
        recipe.setSteps(recipeDTO.getSteps());
        recipe.setLocation(recipe.getLocation());
        return recipe;
    }

    public List<RecipePersonnalised> dtosToEntities(List<RecipePersonnalisedDTO> recipesDTO) {
        return recipesDTO.stream().map(x -> dtoToEntity(x)).collect(Collectors.toList());
    }
}
