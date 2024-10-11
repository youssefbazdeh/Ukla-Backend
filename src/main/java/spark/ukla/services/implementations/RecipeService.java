package spark.ukla.services.implementations;

import javassist.NotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.DTO.RecipeCardDTO;
import spark.ukla.DTO.RecipeDTO;
import spark.ukla.DTO.ViewRecipeDTO;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.ad_feature.ingrediantAd.IngredientAd;
import spark.ukla.ad_feature.ingrediantAd.IngredientAdService;
import spark.ukla.converters.RecipeConverter;
import spark.ukla.creator_feature.*;

import spark.ukla.entities.*;
import spark.ukla.entities.enums.Status;
import spark.ukla.entities.groceyList.GroceryRecipe;
import spark.ukla.repositories.*;
import spark.ukla.repositories.projection.ViewRecipeProjection;
import spark.ukla.services.interfaces.IRecipeService;
import spark.ukla.utils.EmailService;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeService implements IRecipeService {


	@Autowired
	RecipeRepository recipeRepository;
	@Autowired
	FileLocationService fileLocationService;
	@Autowired
	IngredientQuantityObjectRepository ingredientQuantityObjectRepository;

	@Autowired
	IngredientRepository ingredientRepository;

	@Autowired
	CreatorRepository creatorRepository;

	@Autowired
	RecipeConverter recipeConverter;

	@Autowired
	MealRepository mealRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	GroceryRecipeRepository groceryRecipeRepository;
	@Autowired
	GroceryDayRepository groceryDayRepository;

	@Autowired
	StepRepository stepRepository;
	@Autowired
	TagRepository tagRepository ;
	@Autowired
	ReviewRepository reviewRepository;
	@Autowired
	EmailService emailService;
	@Autowired
	IngredientAdService ingredientAdService;
	@Autowired
	ImageDbRepository imageDbRepository;



@Autowired
VideoRepository videoRepository ;
	@Autowired
	private AllergiesRepository allergiesRepository;

	private final CreatorConverter creatorConverter;

	public RecipeService(CreatorConverter creatorConverter) {
		this.creatorConverter = creatorConverter;
	}
	@Override
	public Boolean existsByName(String name) {
		return recipeRepository.existsByName(name) ;
	}



		public void AddRecipeToAllergies(Recipe recipe) {
			//get the recipe's ingredients and look for the allergies that contain these ingredients and assign the recipe to the allergy without adding the same recipe twice
			for(Step step : recipe.getSteps()){
				for (IngredientQuantityObject i : step.getIngredientQuantityObjects()) {
					Ingredient ingredient = ingredientRepository.findByName(i.getIngredient().getName());
					List<Allergy> allergies = ingredient.getAllergies();
					for (Allergy allergy : allergies) {
						if (!allergy.getRecipes().contains(recipe)) {
							allergy.getRecipes().add(recipe);
							allergiesRepository.save(allergy);
						}
					}
				}
			}

    }


    public Step retrieveStepById(Long id) {
       return stepRepository.findById(id).orElse(null);

    }

    @Transactional
    public void updateSteps(Recipe existingRecipe, Recipe updatedRecipe, Map selectedVideosMap) {
        List<Step> newStepsList = updatedRecipe.getSteps();
		List<Step> updatedSteps = new ArrayList<>();
		for (int i=0;i<newStepsList.size();i++){
			Step step = newStepsList.get(i); // Get the current step at index i
				if (step.getIngredientQuantityObjects() != null) {
					List<IngredientQuantityObject> stepIngredientsList = step.getIngredientQuantityObjects();
					if (!stepIngredientsList.isEmpty()) {
						for (IngredientQuantityObject ingredientQuantityObject : stepIngredientsList) {
							Ingredient ingredientExist = ingredientRepository.findByName(ingredientQuantityObject.getIngredient().getName());
							if (ingredientExist != null) {
								ingredientQuantityObject.setIngredient(ingredientExist);
							}
						}
					}
					String key = String.valueOf(i);
					if (selectedVideosMap.containsKey(key)) {
						MultipartFile  videoMultipartFile  = (MultipartFile) selectedVideosMap.get(key);
						Video updatedVideo = fileLocationService.saveVideo(videoMultipartFile);
						step.setVideo(updatedVideo);
					}

					step.setIngredientQuantityObjects(stepIngredientsList);
					updatedSteps.add(stepRepository.save(step)) ;
				}

		}

        existingRecipe.setSteps(updatedSteps);
		NutritionalCalculator(existingRecipe) ;
		recipeRepository.save(existingRecipe);
    }
@Transactional
public void updateTags(Recipe existingRecipe, Recipe updatedRecipe){
         	existingRecipe.setTags(updatedRecipe.getTags());
}


    @Transactional
    public Boolean updateRecipe(Recipe updatedRecipe, long id, Map selectedVideosMap) {
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with id: "));
		if (existingRecipe.getStatus()==Status.Accepted){
			existingRecipe.setStatus(Status.Verified);
		}
        existingRecipe.setName(updatedRecipe.getName());
        existingRecipe.setCalories(updatedRecipe.getCalories());
        existingRecipe.setDescription(updatedRecipe.getDescription());
        existingRecipe.setPreparationTime(updatedRecipe.getPreparationTime());
        existingRecipe.setCookingTime(updatedRecipe.getCookingTime());
		existingRecipe.setPortions(updatedRecipe.getPortions());
        this.updateSteps(existingRecipe, updatedRecipe,selectedVideosMap);
		this.updateTags(existingRecipe, updatedRecipe);
        return true;
    }

	public List<Long> getIngredientIdsFromRecipe(ViewRecipeProjection recipe){
		List<Long> idsList = new ArrayList<>();
		for(Step step : recipe.getSteps()){
			for(IngredientQuantityObject ingredientQuantityObject : step.getIngredientQuantityObjects()){
				idsList.add(ingredientQuantityObject.getIngredient().getId());
			}
		}
		return idsList;
	}

	public void setIngredientAdInIngredientIfExists(ViewRecipeProjection recipe, CountryCode countryCode){
		List<Long> idsList = getIngredientIdsFromRecipe(recipe);
		List<IngredientAd> ingredientAds = ingredientAdService.getAllByIngredientIdsAndActiveAndCountryCode(idsList,countryCode);
		if(!ingredientAds.isEmpty()){
			for(Step step : recipe.getSteps()){
				for(IngredientQuantityObject iqo : step.getIngredientQuantityObjects()){
					for(IngredientAd ingredientAd : ingredientAds){
						if(ingredientAd.getIngredientId().equals(iqo.getIngredient().getId())){
							iqo.getIngredient().setIngredientAd(ingredientAd);
						}
					}
				}
			}
		}
	}

	public ViewRecipeDTO retrieveByIdAndCountryCodeWithIngredientsAd(Long id,CountryCode countryCode) {
		ViewRecipeProjection recipe = recipeRepository.findRecipeById(id);
		setIngredientAdInIngredientIfExists(recipe,countryCode);
		if (recipe!=null){
			return recipeConverter.projectionToDTO(recipe);
		}else
			return null;
	}


	@Override
	public ViewRecipeDTO retrieveById(Long id) {
		ViewRecipeProjection recipe = recipeRepository.findRecipeById(id);
		if (recipe!=null){
			return recipeConverter.projectionToDTO(recipe);
		}else
			return null;
	}

	public ViewRecipeDTO retrieveByIdAndCode(Long id,String code) {
		ViewRecipeProjection recipe = recipeRepository.findRecipeById(id);
		if (recipe!=null){
			return recipeConverter.projectionToViewRecipeDTOTranslated(recipe,code);
		}else
			return null;
	}

	@Override
	public ViewRecipeDTO retrieveByName(String name) {
		ViewRecipeProjection retrievedRecipe = recipeRepository.findByName(name);
		return recipeConverter.projectionToDTO(retrievedRecipe);

	}

	@Override
	public List<RecipeDTO> retrieveAll() {
		List<Recipe> recipes = recipeRepository.findAll();
		return recipeConverter.entitiesToDTOS(recipes);
	}
	public long getRecipeCount() {
		return recipeRepository.count();
	}
	public long getReviewCountByStatus(String status) {
		return reviewRepository.countReviewByRecipeStatus(Status.valueOf(status));
	}
	public long getCreatorRecipeCount(String username) {
		return recipeRepository.countByCreatorUsername(username);
	}
	public List<RecipeCardDTO> getAllRecipesWithPagination(int page, int size) {
		Pageable pageable;
		if (page == 0 && size == 0) {
			return recipeConverter.entitiesToRecipeCardDTO(recipeRepository.findAll());

		}

		pageable = PageRequest.of(page - 1, size);

		// Fetch the first 6 recipes with pagination
		Page<Recipe> firstPage = recipeRepository.findAll(pageable);
		return recipeConverter.entitiesToRecipeCardDTO(firstPage.getContent());
	}
	public List<RecipeCardDTO> getOnlyVerifiedOrAcceptedRecipesWithPagination(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<Recipe> firstPage = recipeRepository.findAllVerifiedAndAcceptedRecipes(Arrays.asList(Status.Verified, Status.Accepted), pageable);
		return recipeConverter.entitiesToRecipeCardDTO(firstPage.getContent());
	}
	public List<RecipeCardDTO> getAllCreatorRecipesWithPagination(int page, int size,String username) {
		Creator creator = creatorRepository.findByUsername(username);
		Pageable pageable;
		if (page == 0 && size == 0) {
			return recipeConverter.entitiesToRecipeCardDTO(recipeRepository.findByCreator_Id(creator.getId()));
		}
		pageable = PageRequest.of(page - 1, size);
		// Fetch the first 6 recipes with pagination
		Page<Recipe> firstPage = recipeRepository.findAll(pageable);
		return recipeConverter.entitiesToRecipeCardDTO(recipeRepository.findByCreator_Id(creator.getId()));
	}

	public CreatorDTOForProfile getCreatorProfileWithVerifiedAndAcceptedRecipes(int page, int size, String username, Long idCreator){
		User user= userRepository.findByUsername(username);
		Creator creator= creatorRepository.findById(idCreator).get();
		boolean isFollower = creatorRepository.existsByIdAndFollowersContains(creator.getId(), user);
		List<RecipeCardDTO> recipeCardDTOList= getVerifiedAndAcceptedRecipes(page,size,creator.getId());
		if (isFollower){
			return creatorConverter.entityToCreatorDTO(creator,true,recipeCardDTOList);
		}
		return creatorConverter.entityToCreatorDTO(creator,false,recipeCardDTOList);
	}

	public List<RecipeCardDTO> getVerifiedAndAcceptedRecipes(int page, int size,Long idCreator) {
		Creator creator = creatorRepository.findById(idCreator).get();
		Pageable pageable = PageRequest.of(page - 1, size);

		Page<Recipe> recipes = recipeRepository.findRecipesByCreatorAndEitherStatus(creator.getId(), Status.Accepted, Status.Verified, pageable);

		return recipeConverter.entitiesToRecipeCardDTO(recipes.getContent());
	}

	public List<Map<String, Object>> getReviewsWithPagination(int page, int size,String status) {
		List<Map<String, Object>> reviewsWithCreator = new ArrayList<>();
		List<Recipe> recipes = recipeRepository.findRecipesByStatus(Status.In_review);
		List<Review> reviews = reviewRepository.findReviewsByRecipeStatus(Status.valueOf(status));
		Pageable pageable;
		if (page == 0 && size == 0) {
			for (Review review:reviews
			) {
				String creatorUsername= review.getRecipe().getCreator().getUsername();
				Map<String, Object> reviewWithCreator = new HashMap<>();
				reviewWithCreator.put("review", review);
				reviewWithCreator.put("creatorUsername", creatorUsername);

				reviewsWithCreator.add(reviewWithCreator);
			}
			return reviewsWithCreator;

		}

		pageable = PageRequest.of(page - 1, size);

		// Fetch the first 6 review with pagination
		Page<Review> firstPage = reviewRepository.findReviewsByRecipeStatus(pageable,Status.valueOf(status));
		for (Review review:firstPage
		) {
			String creatorUsername= review.getRecipe().getCreator().getUsername();
			Map<String, Object> reviewWithCreator = new HashMap<>();
			reviewWithCreator.put("review", review);
			reviewWithCreator.put("creatorUsername", creatorUsername);

			reviewsWithCreator.add(reviewWithCreator);
		}
		return reviewsWithCreator;

	}
	public Review getReviewByID(long id){
		Review review = reviewRepository.findById(id).get();
		return review;
	}
	public Review getReviewByRecipeID(Long id){
		Review review = reviewRepository.findReviewsByRecipe_Id(id);
		return review;
	}
	public boolean updateReview (long id ,Long recipeID,String comment,String status) throws NotFoundException {
		if(recipeID == null ){
			return false;
		}
		Review review= reviewRepository.findById(id).get();
		Recipe recipe= recipeRepository.findById(recipeID).get();
		Creator creator=review.getRecipe().getCreator();
		emailService.send(creator.getEmail(), "Recipe "+recipe.getName()+" has been reviewed ",
				emailService.buildReviewEmail(creator.getUsername(),
						"We have recently reviewed your recipe submission.\n" +
								"\n" +
								"Previously, your recipe was marked as "+recipe.getStatus()+". After thorough review and assessment, we have updated the status to "+status+".\n" +
								"\n"
				));

		review.setComment(comment);
		if(Status.valueOf(status)==Status.Accepted){
			setCreatorVerificationBasedOnAcceptedRecipesCount(creator);
		}

		recipe.setStatus(Status.valueOf(status));
		return true ;
	}

	public boolean setReviewInreviewStatus (long id ,Long recipeID,String comment) throws NotFoundException {
		if(recipeID == null ){
			return false;
		}
		Review review= reviewRepository.findById(id).get();
		Recipe recipe= recipeRepository.findById(recipeID).get();
		review.setComment(comment);
		recipe.setStatus(Status.In_review);
		return true ;
	}

	@Override
	public List<RecipeDTO> retrieveByPreparationAndCookingTime(int startPreparationTime, int endPreparationTime, int startCookingTime, int endCookingTime) {
		List<Recipe> recipes = recipeRepository.findByPreparationTimeBetweenAndCookingTimeBetween(startPreparationTime, endPreparationTime, startCookingTime, endCookingTime);
		return recipeConverter.entitiesToDTOS(recipes);
	}



	@Override
	public List<Recipe> retrieveByTag(Set<Tag> tags) {
		return recipeRepository.findByTagsIn(tags) ;
	}


	@Override
	public List<RecipeDTO> retrieveByCaloriesBetween(float calorieMin, float calorieMax) {
		List<Recipe> recipes = recipeRepository.findByCaloriesBetween(calorieMin, calorieMax);
		return recipeConverter.entitiesToDTOS(recipes);
	}
	public void addRecipeToMeal(Long idMeal, List<Long> idRecipe) {   // to do replace it with a single recipe id after veryfing front
		Meal meal = mealRepository.findById(idMeal).orElse(null);
		List<Recipe> recipes = recipeRepository.findAllById(idRecipe);

		for(Recipe recipe:recipes) {
				if (!meal.getRecipes().contains(recipe))
					meal.getRecipes().add(recipe);

		}
	}


	@Override
	public void deleteRecipeFromMeal(Long idMeal,List<Long>idRecipe) {
		Meal meal = mealRepository.findById(idMeal).orElse(null);
		List<Recipe> recipes = recipeRepository.findAllById(idRecipe);
		for(Recipe recipe:recipes){
			meal.getRecipes().remove(recipe);
			mealRepository.save(meal);
		}
	}

	@Override
	public void addFavoriteRecipeToUser(String username, List<Long> idFavoris) {
		User user = userRepository.findByUsername(username);
		Iterable<Recipe> recipes = recipeRepository.findAllById(idFavoris);
		for(Recipe recipe:recipes){
			user.getFavoris().add(recipe);
		}
	}

	@Override
	public void deleteFavoriteRecipeFromUser(String username, List<Long> idFavoris) {
		User user = userRepository.findByUsername(username);
		Iterable<Recipe> recipes = recipeRepository.findAllById(idFavoris);
		for(Recipe recipe:recipes){
				user.getFavoris().remove(recipe);
				userRepository.save(user);
		}
	}


    @Override
    @Transactional

    public void deleteById(Long id) {

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
		for(Step step : recipe.getSteps()){
			for (IngredientQuantityObject quantityObject : step.getIngredientQuantityObjects()) {
				quantityObject.setIngredient(null);
				ingredientQuantityObjectRepository.delete(quantityObject);
			}
			Video stepVideo= step.getVideo();
			if (stepVideo!=null){
				fileLocationService.deleteVideo(stepVideo.getId());
				videoRepository.delete(stepVideo);
			}

		}
		Video video = recipe.getVideo();
		Image image = recipe.getImage();
		if (video!=null){
			fileLocationService.deleteVideo(video.getId());
			videoRepository.delete(video);
		}
		if (image!=null){
			fileLocationService.deleteImage(image.getLocation());
			imageDbRepository.delete(image);
		}

        recipeRepository.delete(recipe);

    }

	public void forceDeleteRecipe(Long id) {
		Recipe recipe = recipeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found"));
		Review review=reviewRepository.findReviewsByRecipe_Id(id);
		if (review!=null){
			reviewRepository.delete(review);
		}
		List<GroceryRecipe> groceryRecipes = groceryRecipeRepository.findGroceryRecipeByRecipe_Id(id);
		if (groceryRecipes !=null){
			for (GroceryRecipe groceryRecipe:groceryRecipes)
			{
				groceryDayRepository.removeGrocery_day_recipesAssociation(groceryRecipe.getId());
				groceryRecipeRepository.delete(groceryRecipe);
			}
		}

		mealRepository.removeMeal_recipesAssociation(id);
		recipeRepository.removeUser_favorisAssociation(id);
		recipeRepository.delete(recipe);


	}


	@Override
	public String updateImage(Image image, Long id) {
		Recipe recipe = recipeRepository.findById(id).orElse(null);
		if(recipe == null)
			return "recipe not found";
		recipe.setImage(image);
		 recipeRepository.save(recipe);
		 return "image updated";
	}

	@Override
	public String updateVideo(Video video, Long id) {
		Recipe recipe = recipeRepository.findById(id).orElse(null);
		if(recipe == null)
			return  "recipe not found";
		recipe.setVideo(video);
		recipeRepository.save(recipe);
		return null;
	}


	@Override
	public List<RecipeCardDTO> searchRecipe(String query) {
		return recipeConverter.entitiesToRecipeCardDTO(recipeRepository.searchRecipe(query));
	}



	@Override
	public void addVideoToRecipe(Long idRecipe, Long idVideo) {
		Video video = videoRepository.findById(idVideo).orElse(null);
		Recipe recipe = recipeRepository.findById(idRecipe).orElse(null);
		recipe.setVideo(video);

	}

	@Override
	//todo optimize method once we reach 500 recipes
	public List<RecipeCardDTO> get3RecipeSuggestionsByMealTagOrRandom(String mealName, String username) {
		List<Tag> tags = (List<Tag>) tagRepository.findAll();
		Tag tag = null;

		// Find the tag that closely matches the mealName
		int i = 0;
		while (i < tags.size()) {
			double similarity;
			JaroWinklerDistance jwd = new JaroWinklerDistance();
			similarity = jwd.apply(mealName, tags.get(i).getTitle());
			if (similarity > 0.75) {
				tag = tags.get(i);
				break;
			}
			i++;
		}

		List<Recipe> recipeSuggestions;
		if (tag != null) {
			// Get recipes by matching tag
			recipeSuggestions = recipeRepository.getAllByTagsTitle(tag.getTitle());
			Collections.shuffle(recipeSuggestions);

			// Add random recipes if less than 3 matching recipes found
			if (recipeSuggestions.size() < 3) {
				List<Recipe> randomRecipes = recipeRepository.randomRecipe();
				int j = 0;
				while (recipeSuggestions.size() < 3 && j < randomRecipes.size()) {
					recipeSuggestions.add(randomRecipes.get(j));
					j++;
				}
			}
		} else {
			// Get random recipes if no matching tag found
			recipeSuggestions = recipeRepository.randomRecipe();
		}
		// Filter out recipes that contain ingredients that trigger the user's allergies
		recipeSuggestions = filterRecipesByUserAllergies(recipeSuggestions, username);
		return recipeConverter.entitiesToRecipeCardDTO(recipeSuggestions).stream().limit(3).collect(Collectors.toList());
	}

	@Override
	public List<RecipeCardDTO> searchRecipefromfavorites(String query , String username ) {
		return recipeConverter.entitiesToRecipeCardDTO(recipeRepository.searchRecipefromfavorites(query , username));
	}
	@Override
	public boolean addVideoToStep(Long idStep, Video video) {
		Step step = stepRepository.findById(idStep).orElse(null);
		if(step!=null){
			step.setVideo(video);
			stepRepository.save(step);
			return true ;
		}
		return false ;
	}
	@Override
	public void NutritionalCalculator(Recipe recipe){
		float calories = 0f;
		float proteins = 0f;
		float fats = 0f;
		float fibers = 0f;
		float sugars = 0f;
		float carbs= 0f;

		for (Step step: recipe.getSteps()) {
			if(!step.getIngredientQuantityObjects().isEmpty())
			{
				for (IngredientQuantityObject ingredientQuantityObject: step.getIngredientQuantityObjects()) {
					double conversionRate = ingredientRepository.getConversionRate(ingredientQuantityObject.getIngredient().getId(),ingredientQuantityObject.getUnit().ordinal()) ;
					calories += (ingredientQuantityObject.getQuantity() / 100) * ingredientQuantityObject.getIngredient().getNbrCalories100gr()* conversionRate ;
					proteins += (ingredientQuantityObject.getQuantity() / 100) * ingredientQuantityObject.getIngredient().getProtein() * conversionRate ;
					fats += (ingredientQuantityObject.getQuantity() / 100) * ingredientQuantityObject.getIngredient().getFat() * conversionRate;
					fibers += (ingredientQuantityObject.getQuantity() / 100) * ingredientQuantityObject.getIngredient().getFiber() * conversionRate;
					sugars += (ingredientQuantityObject.getQuantity() / 100) * ingredientQuantityObject.getIngredient().getSugar() * conversionRate;
					carbs += (ingredientQuantityObject.getQuantity() / 100) * ingredientQuantityObject.getIngredient().getCarbs() * conversionRate;
				}
			}
		}
		recipe.setCalories(calories/recipe.getPortions());
		recipe.setProtein(proteins/recipe.getPortions());
		recipe.setFat(fats/recipe.getPortions());
		recipe.setFiber(fibers/recipe.getPortions());
		recipe.setSugar(sugars/recipe.getPortions());
		recipe.setCarbs(carbs/recipe.getPortions());
	}

	@Override
	public List<RecipeCardDTO> findByTimeAndTags(int time, Set<Tag> tags) {
		return recipeConverter.entitiesToRecipeCardDTO(recipeRepository.findByTimeAndTags(time, tags));
	}

	@Override
	public String addv2(Recipe recipe, Image image, Video video, Map selectedVideosMap,String username) {

		for(int i=0;i<recipe.getSteps().size();i++){
			Step step = recipe.getSteps().get(i); // Get the current step at index i
			if(step.getIngredientQuantityObjects()!=null) {
				List<IngredientQuantityObject> stepIngredientsList = step.getIngredientQuantityObjects();
				if (!stepIngredientsList.isEmpty()) {
					for (IngredientQuantityObject ing : stepIngredientsList) {
						Ingredient ingredientExist = ingredientRepository.findByName(ing.getIngredient().getName());
						if (ingredientExist != null) {
							ing.setIngredient(ingredientExist);
						}else {
							Ingredient ingredient=ingredientRepository.findIngredientByTranslatedIngredient(ing.getIngredient().getName());
							ing.setIngredient(ingredient);
						}
					}
				}
				String key = String.valueOf(i);
				if (selectedVideosMap.containsKey(key)) {
					MultipartFile videoMultipartFile  = (MultipartFile) selectedVideosMap.get(key);
					Video video1 = fileLocationService.saveVideo(videoMultipartFile);
					step.setVideo(video1);
				}
			}
		}
		User user = userRepository.findByUsername(username);

		if (user instanceof Creator) {
			Creator creator = (Creator) user;
			setRecipeStatusBasedOnCreatorVerification(creator,recipe);
			recipe.setCreator(creator);
		} else {
			System.err.println("User " + username + " is not a Creator.");
		}

		recipe.setImage(image);
		recipe.setVideo(video);
		NutritionalCalculator(recipe);
		recipeRepository.save(recipe);
		AddRecipeToAllergies(recipe);
		createReviewIfCreatorIsNotVerified((Creator) user,recipe);
		return "saved";
	}
	public void setCreatorVerificationBasedOnAcceptedRecipesCount(Creator creator){
		int i=0;
		List<Recipe> recipes=creator.getCreatedRecipe();
		for (Recipe rec:recipes
		) {
			if (rec.getStatus()==Status.Accepted){
				i++;
			}
			if (i==3){
				creator.setVerified(true);
			}
		}
		creatorRepository.save(creator);

	}
	public void setRecipeStatusBasedOnCreatorVerification(Creator creator,Recipe recipe){
		if (!creator.isVerified()){
			Review review = new Review();
			review.setRecipe(recipe);
			recipe.setStatus(Status.In_review);
		}else {
			recipe.setStatus(Status.Verified);
		}
	}
	public void createReviewIfCreatorIsNotVerified(Creator creator,Recipe recipe) {
		if (!creator.isVerified()) {
			Review review = new Review();
			review.setRecipe(recipe);
			reviewRepository.save(review);
		}
	}

    @Transactional
    public void deleteStepFromRecipe(Long recipeId, Long stepId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        Step stepToDelete = stepRepository.findById(stepId)
                .orElseThrow(() -> new EntityNotFoundException("Step not found"));

        if (recipe.getSteps().contains(stepToDelete)) {
            for (IngredientQuantityObject ingredientQuantityObject : stepToDelete.getIngredientQuantityObjects()) {
                ingredientQuantityObject.setIngredient(null);

                ingredientQuantityObjectRepository.delete(ingredientQuantityObject);

            }
            recipe.getSteps().remove(stepToDelete);
            stepRepository.delete(stepToDelete);


            recipeRepository.save(recipe);
        }
    }


	public List<RecipeCardDTO> getFavorite(String username, Pageable pageable){
		return recipeConverter.entitiesToRecipeCardDTO(recipeRepository.getFavorite(username,pageable).getContent());
	}

	public boolean incrementViews (Long recipeId){
		Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
		if(recipeOptional.isPresent()){
			Recipe recipe = recipeOptional.get();
			recipe.incrementViews();
			recipeRepository.save(recipe);
			return true;
		} else
			return false;
	}

	private List<Recipe> filterRecipesByUserAllergies(List<Recipe> recipes, String username) {
		// Fetch the user's allergies
		User user = userRepository.findByUsername(username);
		List<Allergy> allergies = allergiesRepository.findAllergiesByUserId(user.getId());
		Set<String> allergicIngredients = new HashSet<>();
		for (Allergy allergy : allergies) {
			for (Ingredient ingredient : allergy.getIngredients()) {
				allergicIngredients.add(ingredient.getName());
			}
		}
		System.out.println(allergicIngredients);

		// Filter out recipes that contain any allergic ingredients
		return recipes.stream()
				.filter(recipe -> recipe.getSteps().stream()
						.flatMap(step -> step.getIngredientQuantityObjects().stream())
						.map(ingredientQuantity -> ingredientQuantity.getIngredient().getName())
						.noneMatch(allergicIngredients::contains))
				.collect(Collectors.toList());
	}
}
