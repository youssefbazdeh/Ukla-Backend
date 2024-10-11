package spark.ukla.services.implementations;

import org.springframework.stereotype.Service;
import spark.ukla.entities.Recipe;
import spark.ukla.entities.User;
import spark.ukla.repositories.RecipeRepository;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.interfaces.IFavorisService;
import javax.transaction.Transactional;


@Service
@Transactional
public class FavorisService implements IFavorisService{

	private final UserRepository userRepository;

	private final RecipeRepository recipeRepository;

	public FavorisService(UserRepository userRepository, RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
	}

	@Override
	public String add(String username, long recipeId) {
		User user = userRepository.findByUsername(username) ;
 		Recipe recipeExists = recipeRepository.findById(recipeId).get();
		user.getFavoris().add(recipeExists);
		return "recipe added to favoris";
	}

	@Override
	public String delete(String username, long recipeId) {
		User user = userRepository.findByUsername(username) ;
		Recipe recipeExists = recipeRepository.findById(recipeId).get();
		user.getFavoris().remove(recipeExists);
		return "recipe deleted from your favoris";
	}

	@Override
	public boolean isRecipeLikedByUser(String username, long recipeId) {
		return userRepository.isRecipeInFavorites(username, recipeId);
	}

}
