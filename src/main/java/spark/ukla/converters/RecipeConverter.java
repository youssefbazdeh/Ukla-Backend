package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.*;
import spark.ukla.creator_feature.CreatorConverter;
import spark.ukla.entities.*;
import spark.ukla.repositories.projection.ViewRecipeProjection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class RecipeConverter {

	private final CreatorConverter creatorConverter;
	private final StepConverter stepConverter;

	public RecipeConverter(CreatorConverter creatorConverter, StepConverter stepConverter) {
        this.creatorConverter = creatorConverter;
        this.stepConverter = stepConverter;
    }

	public String getTranslatedOrDefaultName(Ingredient ingredient, String languageCode) {
		List<TranslatedIngredient> translatedNames = ingredient.getTranslatedIngredients();
		for (TranslatedIngredient translatedIngredient : translatedNames) {
			if (translatedIngredient.getLanguageCode().equals(languageCode)) {
				return translatedIngredient.getName();
			}
		}
		return ingredient.getName();
	}

	public ViewRecipeDTO projectionToViewRecipeDTOTranslated(ViewRecipeProjection viewRecipeProjection, String languageCode){
		ViewRecipeDTO viewRecipeDTO = new ViewRecipeDTO();
		List<StepDTO> stepDTOS = new ArrayList<>();
		for(Step step : viewRecipeProjection.getSteps()){
			StepDTO stepDTO = new StepDTO();
			List<IngredientQuantityObjectDTO> ingredientQuantityObjectDTOSSteps = new ArrayList<>();
			for (IngredientQuantityObject ingredientQuantityObject : step.getIngredientQuantityObjects()){
				Ingredient ingredient = ingredientQuantityObject.getIngredient();
				String translatedName = getTranslatedOrDefaultName(ingredient,languageCode);
				IngredientDTO ingredientDTO = new IngredientDTO();
				ingredientDTO.setName(translatedName);
				ingredientDTO.setId(ingredient.getId());
				ingredientDTO.setType(ingredient.getType());
				ingredientDTO.setImage(ingredient.getImage());
				ingredientDTO.setUnitAlternatives(ingredient.getUnitAlternatives());
				IngredientQuantityObjectDTO ingredientQuantityObjectDTO = new IngredientQuantityObjectDTO();
				ingredientQuantityObjectDTO.setId(ingredientQuantityObject.getId());
				ingredientQuantityObjectDTO.setQuantity(ingredientQuantityObject.getQuantity());
				ingredientQuantityObjectDTO.setIngredient(ingredientDTO);
				ingredientQuantityObjectDTO.setUnit(ingredientQuantityObject.getUnit());
				ingredientQuantityObjectDTOSSteps.add(ingredientQuantityObjectDTO);
			}
			stepDTO.setTip(step.getTip());
			stepDTO.setInstruction(step.getInstruction());
			stepDTO.setVideo(step.getVideo());
			stepDTO.setId(step.getId());
			stepDTO.setIngredientQuantityObjects(ingredientQuantityObjectDTOSSteps);
			stepDTOS.add(stepDTO);
		}

		viewRecipeDTO.setId(viewRecipeProjection.getId());
		viewRecipeDTO.setName(viewRecipeProjection.getName());
		viewRecipeDTO.setDescription(viewRecipeProjection.getDescription());
		viewRecipeDTO.setPreparationTime(viewRecipeProjection.getPreparationTime());
		viewRecipeDTO.setCookingTime(viewRecipeProjection.getCookingTime());
		viewRecipeDTO.setPortions(viewRecipeProjection.getPortions());
		viewRecipeDTO.setCalories(viewRecipeProjection.getCalories());
		viewRecipeDTO.setProtein(viewRecipeProjection.getProtein());
		viewRecipeDTO.setFat(viewRecipeProjection.getFat());
		viewRecipeDTO.setCarbs(viewRecipeProjection.getCarbs());
		viewRecipeDTO.setFiber(viewRecipeProjection.getFiber());
		viewRecipeDTO.setSugar(viewRecipeProjection.getSugar());
		viewRecipeDTO.setSteps(stepDTOS);
		viewRecipeDTO.setVideo(viewRecipeProjection.getVideo());
		viewRecipeDTO.setImage(viewRecipeProjection.getImage());
		viewRecipeDTO.setTags(viewRecipeProjection.getTags());
		viewRecipeDTO.setFavorite(viewRecipeProjection.isFavorite());
		viewRecipeDTO.setCreator(creatorConverter.projectionToDTO(viewRecipeProjection.getCreator()));
		return viewRecipeDTO;
	}

	public ViewRecipeDTO projectionToDTO(ViewRecipeProjection viewRecipeProjection){
		ViewRecipeDTO viewRecipeDTO = new ViewRecipeDTO();
		viewRecipeDTO.setId(viewRecipeProjection.getId());
		viewRecipeDTO.setName(viewRecipeProjection.getName());
		viewRecipeDTO.setDescription(viewRecipeProjection.getDescription());
		viewRecipeDTO.setPreparationTime(viewRecipeProjection.getPreparationTime());
		viewRecipeDTO.setCookingTime(viewRecipeProjection.getCookingTime());
		viewRecipeDTO.setPortions(viewRecipeProjection.getPortions());
		viewRecipeDTO.setCalories(viewRecipeProjection.getCalories());
		viewRecipeDTO.setProtein(viewRecipeProjection.getProtein());
		viewRecipeDTO.setFat(viewRecipeProjection.getFat());
		viewRecipeDTO.setCarbs(viewRecipeProjection.getCarbs());
		viewRecipeDTO.setFiber(viewRecipeProjection.getFiber());
		viewRecipeDTO.setSugar(viewRecipeProjection.getSugar());
		viewRecipeDTO.setSteps(stepConverter.entitiestoDTO(viewRecipeProjection.getSteps()));
		viewRecipeDTO.setVideo(viewRecipeProjection.getVideo());
		viewRecipeDTO.setImage(viewRecipeProjection.getImage());
		viewRecipeDTO.setTags(viewRecipeProjection.getTags());
		viewRecipeDTO.setFavorite(viewRecipeProjection.isFavorite());
		viewRecipeDTO.setCreator(creatorConverter.projectionToDTO(viewRecipeProjection.getCreator()));
		return viewRecipeDTO;
	}
	public ViewRecipeDTO entityToViewRecipeDTO(Recipe recipe) {
		ViewRecipeDTO dto = new ViewRecipeDTO();
		dto.setId(recipe.getId());
		dto.setName(recipe.getName());
		dto.setDescription(recipe.getDescription());
		dto.setPreparationTime(recipe.getPreparationTime());
		dto.setCookingTime(recipe.getCookingTime());
		dto.setCalories(recipe.getCalories());
		dto.setProtein(recipe.getProtein());
		dto.setFat(recipe.getFat());
		dto.setCarbs(recipe.getCarbs());
		dto.setFiber(recipe.getFiber());
		dto.setSugar(recipe.getSugar());
		dto.setSteps(stepConverter.entitiestoDTO(recipe.getSteps()));
		dto.setVideo(recipe.getVideo());
		dto.setImage(recipe.getImage());
		dto.setCreator(creatorConverter.entityToCreatorDTOForRecipe(recipe.getCreator()));
		dto.setTags(recipe.getTags());
		return dto;
	}

	public RecipeCardDTO entityToRecipeCardDTO(Recipe recipe) {
		RecipeCardDTO dto = new RecipeCardDTO();
		dto.setId(recipe.getId());
		dto.setName(recipe.getName());
		dto.setCookingTime(recipe.getCookingTime());
		dto.setPreparationTime(recipe.getPreparationTime());
		dto.setCalories(recipe.getCalories());
		dto.setImage(recipe.getImage());
		dto.setTags(recipe.getTags());
		dto.setStatus(recipe.getStatus());
		return dto;
	}

	public RecipeDTO entityToDTO(Recipe recipe) {    //todo check the todo below
		RecipeDTO dto = new RecipeDTO();
		dto.setId(recipe.getId());
		dto.setName(recipe.getName());
		dto.setDescription(recipe.getDescription());
		dto.setPreparationTime(recipe.getPreparationTime());
		dto.setCookingTime(recipe.getCookingTime());
		dto.setPortions(recipe.getPortions());
		dto.setCalories(recipe.getCalories());
		dto.setProtein(recipe.getProtein());
		dto.setFat(recipe.getFat());
		dto.setCarbs(recipe.getCarbs());
		dto.setFiber(recipe.getFiber());
		dto.setSugar(recipe.getSugar());
		dto.setSteps(recipe.getSteps());
		dto.setVideo(recipe.getVideo());
		dto.setImage(recipe.getImage());
		dto.setCreator(recipe.getCreator());
		dto.setTags(recipe.getTags());
		return dto;
	}

	public List<RecipeDTO> entitiesToDTOS(List<Recipe> recipes) {       //todo check this method along with recipeDTO probably needs to be deleted and replace it's usages with recipeCardDTOs
		return recipes.stream().map(x -> entityToDTO(x)).collect(Collectors.toList());
	}

	public List<RecipeCardDTO> entitiesToRecipeCardDTO(List<Recipe> recipes) {
		return recipes.stream().map(x -> entityToRecipeCardDTO(x)).collect(Collectors.toList());
	}


}
