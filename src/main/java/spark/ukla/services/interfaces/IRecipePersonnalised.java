package spark.ukla.services.interfaces;



import spark.ukla.DTO.RecipePersonnalisedDTO;
import spark.ukla.entities.Image;
import spark.ukla.entities.RecipePersonnalised;

import java.util.List;


public interface IRecipePersonnalised {


    String add(RecipePersonnalisedDTO personnalisedDTO, Image image);

    RecipePersonnalisedDTO update(RecipePersonnalisedDTO personnalisedDTO);

    RecipePersonnalised retrieveById(Long id);

    List<RecipePersonnalisedDTO> retrieveAll();

    String deleteById(Long id);

    List<RecipePersonnalisedDTO> retrieveByToAvoid(String toAvoid);

    List<RecipePersonnalisedDTO> retrieveByToRecommend(String toRecommend);

    List<RecipePersonnalisedDTO> retrieveByNbrCaloriesBetween(float calorieMin, float calorieMax);

    void addFavoriteRecipePersonalisedToUser(String username, List<Long> idFavorisPerson);

    void deleteFavoriteRecipePersonalisedFromUser(String username, List<Long> idFavorisPerson);


    List<RecipePersonnalisedDTO> retrieveByPreparationTime(int startPreparationTime, int endPreparationTime);

	List<RecipePersonnalisedDTO> retrieveByCookingTime(int startCookingTime, int endCookingTime);

	List<RecipePersonnalisedDTO> retrieveByPreparationAndCookingTime(int startPreparationTime, int endPreparationTime, int startCookingTime, int endCookingTime);

    String updateImage(Image image, Long id);

    List<RecipePersonnalised> searchRecipePersonalised(String query);




}

