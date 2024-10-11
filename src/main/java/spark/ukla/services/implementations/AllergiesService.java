package spark.ukla.services.implementations;

import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import spark.ukla.entities.Allergy;
import spark.ukla.entities.Image;
import spark.ukla.entities.Ingredient;
import spark.ukla.entities.User;
import spark.ukla.repositories.AllergiesRepository;
import spark.ukla.repositories.IngredientRepository;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.interfaces.IAllergiesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AllergiesService implements IAllergiesService {
    private final AllergiesRepository allergiesRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;

    public AllergiesService(AllergiesRepository allergiesRepository, UserRepository userRepository,
                            IngredientRepository ingredientRepository) {
        this.allergiesRepository = allergiesRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public Boolean add(String name,List<Long> IngredientIds, Image image) {
        Allergy newAllergy = new Allergy();
        List<Ingredient> ingredients = new ArrayList<>();
        for (Long IngredientId : IngredientIds) {
            Ingredient ingredient= ingredientRepository.findById(IngredientId).get();
            ingredients.add(ingredient);
        }
        newAllergy.setName(name);
        newAllergy.setIngredients(ingredients);
        newAllergy.setImage(image);
        allergiesRepository.save(newAllergy);
        return true;
    }


    @Override
    public List<Allergy> getAll() {
        return allergiesRepository.findAll();
    }

    @Override
    public List<Allergy> getAllByUserId(String username) {
        User user = userRepository.findByUsername(username);
        return allergiesRepository.findAllergiesByUserId(user.getId());
    }

    @Override
    public Boolean addAllergiesIds(List<Long> ids, String username) {
        //check if allergy is assigned to user
        User user = userRepository.findByUsername(username);
        System.out.println(ids);
        //check if there is an allergy deleted
        List<Allergy> ExistingAllergies = allergiesRepository.findAllergiesByUserId(user.getId());
        System.out.println(ExistingAllergies.size());
        for (Allergy ExistingAllergy : ExistingAllergies) {
            System.out.println(ExistingAllergy.getId());
            System.out.println(ids.contains(ExistingAllergy.getId()));
            if (ids.contains(ExistingAllergy.getId())) {
                System.out.println("inside if");
                List<User> users = ExistingAllergy.getUsers();
                System.out.println("users list length: "+users.size());
                users.remove(user);
                System.out.println("user removed");
                ExistingAllergy.setUsers(users);
                System.out.println("updated");
            }
        }
        allergiesRepository.saveAll(ExistingAllergies);
        //check if there is an allergy added
        for (Long id : ids) {
            Optional<Allergy> allergy = allergiesRepository.findById(id);
            List<User> users = allergy.get().getUsers();
            if (!users.contains(user)) {
                users.add(user);
                allergy.get().setUsers(users);
                allergiesRepository.save(allergy.get());
            }
        }
        return true;
    }

    public Allergy retrieveById(Long id) {
        return allergiesRepository.findById(id).get();
    }

    @Override
    public boolean delete(Long id) {
        allergiesRepository.deleteById(id);
        return true;
    }

    public Boolean deleteAllergiesByUserId(Long id, String username) {
        User user = userRepository.findByUsername(username);
        Allergy allergy = allergiesRepository.findById(id).get();
        List<User> users = allergy.getUsers();
        users.remove(user);
        allergy.setUsers(users);
        allergiesRepository.save(allergy);
        return true;
    }
    @Override
    public Boolean existsByName(String name) {
        return allergiesRepository.existsByName(name) ;
    }

    @Override
    public Boolean update(Allergy allergy,List<Long> IngredientIds, Image image) {
 Allergy EditAllergy=allergiesRepository.findById(allergy.getId()).get();
            List<Ingredient> ingredients = new ArrayList<>();
        if (!IngredientIds.isEmpty()){
            for (Long IngredientId : IngredientIds) {
                Ingredient ingredient= ingredientRepository.findById(IngredientId).get();
                ingredients.add(ingredient);
            }
            EditAllergy.setIngredients(ingredients);
        }
        EditAllergy.setName(allergy.getName());
        EditAllergy.setImage(image);
            allergiesRepository.save(EditAllergy);
            return true;
        }

    @Override
    public Boolean updateImage(Image image, long id) {

        Allergy allergy = allergiesRepository.findById(id).orElse(null);
        if (allergy == null)
            return false;
        allergy.setImage(image);
        allergiesRepository.save(allergy);
        return true;

    }


}
