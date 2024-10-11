package spark.ukla.creator_feature;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.DTO.RecipeCardDTO;
import spark.ukla.converters.RecipeConverter;
import spark.ukla.entities.ConfirmationToken;
import spark.ukla.entities.Image;
import spark.ukla.entities.Recipe;
import spark.ukla.entities.User;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.implementations.ConfirmationTokenService;
import spark.ukla.utils.EmailService;
import spark.ukla.utils.RandomUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional
public class CreatorService {

    private final CreatorRepository creatorRepository;
    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService ;
    private final EmailService emailService;
    private final CreatorConverter creatorConverter;
    private final RecipeConverter recipeConverter;

    public CreatorService(CreatorRepository creatorRepository, UserRepository userRepository, ConfirmationTokenService confirmationTokenService, EmailService emailService, CreatorConverter creatorConverter, RecipeConverter recipeConverter) {
        this.creatorRepository = creatorRepository;
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.creatorConverter = creatorConverter;
        this.recipeConverter = recipeConverter;
    }

    public Boolean updateImage(Image image, String username) {
        Creator creator = creatorRepository.findByUsername(username);
        if(creator == null)
            return false ;
        creator.setImage(image);
        creatorRepository.save(creator);
        return  true;
    }
    public ResponseEntity<String> updateCreatorProfile(Creator updatedProfile, String username) {
        Creator existingProfile =  creatorRepository.findByUsername(username);

        existingProfile.setDescription(updatedProfile.getDescription());
        existingProfile.setBirthdate(updatedProfile.getBirthdate());
        existingProfile.setFirstName(updatedProfile.getFirstName());
        existingProfile.setLastName(updatedProfile.getLastName());
        existingProfile.setGender(updatedProfile.getGender());
        if (!existingProfile.getUsername().equals(updatedProfile.getUsername()) && !existingProfile.getEmail().equals(updatedProfile.getEmail())){
            boolean creatorUsernameExists  = creatorRepository.existsByUsername(updatedProfile.getUsername());
            boolean userUsernameExists= userRepository.existsByUsername(updatedProfile.getUsername());
            boolean userEmailExists = userRepository.existsByEmail(updatedProfile.getEmail());
            boolean creatorEmailExists = creatorRepository.existsByEmail(updatedProfile.getEmail());

            if (userUsernameExists || creatorUsernameExists ) {
                return new ResponseEntity<>("username exists", HttpStatus.FOUND);
            }
            if (userEmailExists || creatorEmailExists )  {
                return new ResponseEntity<>("email exists", HttpStatus.NOT_ACCEPTABLE);
            }
            String token = RandomUtil.generateRandomStringNumber(6).toUpperCase();
            ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(5));
            confirmationTokenService.add(confirmationToken);
            emailService.send(updatedProfile.getEmail(), "Confirm your email.", emailService.buildEmail(updatedProfile.getUsername(),
                    "Thank you for updating your profile. Please activate your account:", token));
            log.info("User saved, confirm your email.");

            existingProfile.setUsername(updatedProfile.getUsername());
            existingProfile.setEmail(updatedProfile.getEmail());
            return new ResponseEntity<>("user saved", HttpStatus.MULTI_STATUS);
        }

        if (!existingProfile.getUsername().equals(updatedProfile.getUsername())){
            boolean creatorUsernameExists  = creatorRepository.existsByUsername(updatedProfile.getUsername());
            boolean userUsernameExists= userRepository.existsByUsername(updatedProfile.getUsername());
            if (userUsernameExists || creatorUsernameExists ) {
                return new ResponseEntity<>("username exists", HttpStatus.FOUND);
            }
            existingProfile.setUsername(updatedProfile.getUsername());
            return new ResponseEntity<>("user saved", HttpStatus.ACCEPTED);
        }

        if(!existingProfile.getEmail().equals(updatedProfile.getEmail())){
            boolean creatorEmailExists = creatorRepository.existsByEmail(updatedProfile.getEmail());
            boolean userEmailExists = userRepository.existsByEmail(updatedProfile.getEmail());
            if (userEmailExists || creatorEmailExists )  {
                return new ResponseEntity<>("email exists", HttpStatus.NOT_ACCEPTABLE);
            }
            String token = RandomUtil.generateRandomStringNumber(6).toUpperCase();
            ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(5));
            confirmationTokenService.add(confirmationToken);
            emailService.send(updatedProfile.getEmail(), "Confirm your email.", emailService.buildEmail(updatedProfile.getUsername(),
                    "Thank you for updating your profile. Please activate your account:", token));
            log.info("User saved, confirm your email.");
            existingProfile.setEmail(updatedProfile.getEmail());
            return new ResponseEntity<>("user saved", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("user saved", HttpStatus.OK);
    }

    public boolean followCreator(String username, Long creatorId){
        User user= userRepository.findByUsername(username);
        Creator creator = creatorRepository.findById(creatorId).get();

        creatorRepository.addFollower(creatorId,user.getId());
        creator.incrementFollowers();
        return true;
    }

    public boolean unfollowCreator(String username, Long creatorId){
        User user= userRepository.findByUsername(username);
        Creator creator = creatorRepository.findById(creatorId).get();

        creatorRepository.removeCreator_followersAssociation(user.getId());
        creator.decrementFollowers();
        return true;

    }

    public CreatorDTOForProfile getCreatorByIdForProfile(Long id,String username){
        User user = userRepository.findByUsername(username);
        Creator creator = creatorRepository.getById(id);
        boolean isFollowed = creator.getFollowers().contains(user);
        List<RecipeCardDTO> creatorRecipes = recipeConverter.entitiesToRecipeCardDTO(creator.getCreatedRecipe());
        Set<Recipe> usersFavoriteRecipesList = user.getFavoris();
        for (RecipeCardDTO recipeCard : creatorRecipes) {
            boolean isFavorite = usersFavoriteRecipesList.stream()
                    .anyMatch(favoriteRecipe -> favoriteRecipe.getId().equals(recipeCard.getId()));
            recipeCard.setRecipeInUserFavorites(isFavorite);
        }
        return creatorConverter.entityToCreatorDTO(creator,isFollowed,creatorRecipes);
    }
}
