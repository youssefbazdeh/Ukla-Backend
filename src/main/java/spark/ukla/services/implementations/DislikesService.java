package spark.ukla.services.implementations;

import org.springframework.stereotype.Service;
import spark.ukla.entities.Ingredient;
import spark.ukla.entities.User;
import spark.ukla.repositories.IngredientRepository;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.interfaces.IDislikesService;

import java.util.List;

@Service
public class DislikesService implements IDislikesService {
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;

    public DislikesService(IngredientRepository ingredientRepository, UserRepository userRepository) {
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
    }
    @Override
    public boolean addDislikedIngredient(String username, Long ingredientId){
        User user = userRepository.findByUsername(username);
        ingredientRepository.findById(ingredientId).ifPresent(ingredient -> {
            user.getDislikes().add(ingredient);
            userRepository.save(user);
            });
        return true;
    }
    @Override
    public boolean addDislikedIngredients(String username, List<Long> ingredientIds){
        User user = userRepository.findByUsername(username);
        ingredientIds.forEach(ingredientId -> {
            ingredientRepository.findById(ingredientId).ifPresent(ingredient -> {
                user.getDislikes().add(ingredient);
                userRepository.save(user);
            });
        });
        return true;
    }
    @Override
    public boolean removeDislikedIngredient(String username, Long ingredientId){
        User user = userRepository.findByUsername(username);
        ingredientRepository.findById(ingredientId).ifPresent(ingredient -> {
            user.getDislikes().remove(ingredient);
            userRepository.save(user);
        });
        return true;
    }
    @Override
    public List<Ingredient> getAllDislikedIngredients(String username){
        User user = userRepository.findByUsername(username);
        return user.getDislikes();
    }
}
