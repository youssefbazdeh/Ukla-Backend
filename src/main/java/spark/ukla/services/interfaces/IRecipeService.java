package spark.ukla.services.interfaces;

import spark.ukla.DTO.RecipeCardDTO;
import spark.ukla.DTO.RecipeDTO;
import spark.ukla.DTO.ViewRecipeDTO;
import spark.ukla.entities.Image;
import spark.ukla.entities.Recipe;
import spark.ukla.entities.Tag;
import spark.ukla.entities.Video;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRecipeService {
	Boolean existsByName(String name) ;

	List<RecipeCardDTO> findByTimeAndTags(int time, Set<Tag> tags);

	String addv2(Recipe recipe, Image image, Video video, Map selectedVideosMap,String username);
	ViewRecipeDTO retrieveById(Long id);

	ViewRecipeDTO retrieveByName(String name);

	List<RecipeDTO> retrieveByPreparationAndCookingTime(int startPreparationTime, int endPreparationTime, int startCookingTime, int endCookingTime);

	List<Recipe> retrieveByTag(Set<Tag> tags);
	
	List<RecipeDTO> retrieveByCaloriesBetween(float calorieMin, float calorieMax);

	List<RecipeDTO> retrieveAll();

	void addRecipeToMeal(Long idMeal, List<Long> idRecipe);

	void deleteRecipeFromMeal(Long idMeal, List<Long> idRecipe);

	void addFavoriteRecipeToUser(String username, List<Long> idFavoris);

	void deleteFavoriteRecipeFromUser(String username, List<Long> idFavoris);

	void deleteById(Long id);


	String updateImage(Image image, Long id);

	String updateVideo(Video video, Long id);


	List<RecipeCardDTO> searchRecipe(String query);
	List<RecipeCardDTO> searchRecipefromfavorites(String query , String username );
	void addVideoToRecipe(Long idRecipe, Long idVideo);
	List<RecipeCardDTO> get3RecipeSuggestionsByMealTagOrRandom(String mealName,String username);
	boolean addVideoToStep(Long idStep, Video video);

    void NutritionalCalculator(Recipe recipe);
}
