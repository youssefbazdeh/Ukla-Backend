package spark.ukla.controllers;

import javassist.NotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.DTO.RecipeCardDTO;
import spark.ukla.DTO.RecipeDTO;
import spark.ukla.DTO.ViewRecipeDTO;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.converters.RecipeConverter;
import spark.ukla.creator_feature.Creator;
import spark.ukla.creator_feature.CreatorDTOForProfile;
import spark.ukla.entities.*;
import spark.ukla.repositories.RecipeRepository;
import spark.ukla.services.implementations.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/Recipe")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeController {

	private final RecipeService recipeService;
	private final RecipeRepository recipeRepository;
	private final UserService userService;
	private final FileLocationService fileLocationService;
	private final RecipeConverter recipeConverter;
	private final TagService tagService;

	private final FavorisService favorisService;


    public RecipeController(RecipeService recipeService, RecipeRepository recipeRepository, UserService userService, FileLocationService fileLocationService, RecipeConverter recipeConverter, TagService tagService, FavorisService favorisService) {
		this.recipeService = recipeService;
		this.recipeRepository = recipeRepository;
		this.userService = userService;
		this.fileLocationService = fileLocationService;
		this.recipeConverter = recipeConverter;
        this.tagService = tagService;
        this.favorisService = favorisService;
    }


	@GetMapping("/count")
	public ResponseEntity<Long> getRecipeCount() {
		long count = recipeService.getRecipeCount();
		return ResponseEntity.ok(count);
	}

	@GetMapping("/countCreatorRecipes")
	public ResponseEntity<Long> getCreatorRecipeCount(@RequestHeader("AUTHORIZATION") String header) {
		String username = userService.getusernamefromtoken(header);

		long count = recipeService.getCreatorRecipeCount(username);
		return ResponseEntity.ok(count);
	}

	@PutMapping("update/{id}")
	public ResponseEntity<String> updateRecipe(@RequestPart Recipe recipe,
											   @PathVariable Long id,
											   @RequestParam Map<String, MultipartFile> selectedVideos) {
		if (recipeService.updateRecipe(recipe,id,selectedVideos)) {

			return new ResponseEntity<>("updated", HttpStatus.OK);

		} else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);

	}

    @GetMapping("/getStepById/{id}")
    public ResponseEntity<Step> retrieveStepById(@PathVariable Long id) {
        Step recipeDTO = recipeService.retrieveStepById(id);
        if (recipeDTO != null)
            return new ResponseEntity<>(recipeDTO, HttpStatus.OK);
        else
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

	@GetMapping("/countReview/{status}")
	public ResponseEntity<Long> getReviewCount(@PathVariable String status) {
		long count = recipeService.getReviewCountByStatus(status);
		return ResponseEntity.ok(count);
	}
	@GetMapping("/getReviews/{pageNo}/{pageSize}/{status}")
	public ResponseEntity<List<Map<String, Object>>> getReviews(@PathVariable int pageNo,
																@PathVariable int pageSize,
																@PathVariable String status) {
		List<Map<String, Object>> reviewsWithCreator = recipeService.getReviewsWithPagination(pageNo,pageSize,status);
		if (reviewsWithCreator!= null &&!reviewsWithCreator.isEmpty()) {
			return new ResponseEntity<>(reviewsWithCreator, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping("/getReviewByRecipeID/{id}")
	public ResponseEntity<Review> getReviewByRecipeID(@PathVariable Long id) {
		Review review = recipeService.getReviewByRecipeID(id);
		if (review!= null ) {
			return new ResponseEntity<>(review, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	@GetMapping("/getReviewById/{id}")
	public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
		Review review = recipeService.getReviewByID(id);
		if (review!= null) {
			return new ResponseEntity<>(review, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	@PutMapping("/updateReview/{id}")
	public ResponseEntity<String> updateReview(@PathVariable Long id, @RequestParam Long recipeID,
											   @RequestParam String comment, @RequestParam String status) throws NotFoundException {
		boolean saved= recipeService.updateReview(id,recipeID,comment,status);
		if (saved){
			return new ResponseEntity<>("saved",HttpStatus.CREATED);
		}
		else
			return new ResponseEntity<>("recipe not found",HttpStatus.NOT_ACCEPTABLE);

	}

	@PutMapping("/setReviewInreviewStatus/{id}")
	public ResponseEntity<String> setReviewInreviewStatus(@PathVariable Long id, @RequestParam Long recipeID,
											   @RequestParam String comment) throws NotFoundException {
		boolean saved= recipeService.setReviewInreviewStatus(id,recipeID,comment);
		if (saved){
			return new ResponseEntity<>("saved",HttpStatus.CREATED);
		}
		else
			return new ResponseEntity<>("recipe not found",HttpStatus.NOT_ACCEPTABLE);

	}
    @PutMapping("/updateImage/{id}")
    public ResponseEntity<String> updateImage(@RequestParam MultipartFile image, @PathVariable Long id) throws Exception {
        Image image1 = fileLocationService.save(image);
        if (image1 == null) {
            return new ResponseEntity<>("image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        String msg = recipeService.updateImage(image1, id);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

	@PutMapping("/updateVideo/{id}")
	public ResponseEntity<String> updateVideo(@RequestParam MultipartFile video, @PathVariable Long id) {
		Video video1 = fileLocationService.saveVideo(video);
		if(video1 == null) {
			return new ResponseEntity<>("video not found", HttpStatus.NOT_FOUND);
		}
		String msg = recipeService.updateVideo(video1,id);
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}

	@GetMapping("/getById/{id}/{countryCode}")
	public ResponseEntity<?> retrieveById(@PathVariable Long id, @PathVariable("countryCode")String countryCode){
		CountryCode code = CountryCode.valueOf(countryCode);
		ViewRecipeDTO viewRecipeDTO = recipeService.retrieveByIdAndCountryCodeWithIngredientsAd(id,code);
		if(viewRecipeDTO != null)
			return new ResponseEntity<>(viewRecipeDTO, HttpStatus.OK);
		else
			return new ResponseEntity<>("recipe does not exist", HttpStatus.NOT_FOUND);
	}




	@GetMapping("/getByIdAndLanguageCode/{id}/{code}")
	public ResponseEntity<?> retrieveByIdAndLanguageCode(@RequestHeader("AUTHORIZATION") String header,@PathVariable Long id,@PathVariable String code){
		String username = userService.getusernamefromtoken(header);
		ViewRecipeDTO viewRecipeDTO = recipeService.retrieveByIdAndCode(id,code);
		viewRecipeDTO.setFavorite(favorisService.isRecipeLikedByUser(username,viewRecipeDTO.getId()));
		if(viewRecipeDTO != null)
			return new ResponseEntity<>(viewRecipeDTO, HttpStatus.OK);
		else
			return new ResponseEntity<>("recipe does not exist", HttpStatus.NOT_FOUND);
	}


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        recipeService.deleteById(id);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }
	@DeleteMapping("/forceDeleteRecipe/{id}")
	public ResponseEntity<String> forceDeleteRecipe(@PathVariable("id") Long id) {
		recipeService.forceDeleteRecipe(id);
		return new ResponseEntity<>("deleted", HttpStatus.OK);
	}
    @DeleteMapping("/deleteStep/{idRecipe}/{idStep}")
    public ResponseEntity<String> deleteStep(@PathVariable("idRecipe") Long idRecipe,@PathVariable("idStep") Long idStep) {
        recipeService.deleteStepFromRecipe(idRecipe,idStep);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }


	@GetMapping("/retrieveByName/{name}")
	public ResponseEntity<?> retrieveByName(@RequestHeader("AUTHORIZATION") String header,@PathVariable String name) {
		String username = userService.getusernamefromtoken(header);
		ViewRecipeDTO recipeRetrieved = recipeService.retrieveByName(name);
		recipeRetrieved.setFavorite(favorisService.isRecipeLikedByUser(username,recipeRetrieved.getId()));
		if (recipeRetrieved != null)
			return new ResponseEntity<>(recipeRetrieved, HttpStatus.OK);
		else
			return new ResponseEntity<>("recipe does not exist",HttpStatus.NO_CONTENT);
	}

	@PostMapping("/retrieveByTag")
	public ResponseEntity<List<Recipe>> retrieveByTag(@RequestBody Set<Tag> tags) {
		List<Recipe> recipesRetrieved = recipeService.retrieveByTag(tags);
		return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
	}


	@GetMapping("/retrieveByPreparationAndCookingTime/{startPreparation}/{endPreparation}/{startCooking}/{endCooking}")
	public ResponseEntity<List<RecipeDTO>> retrieveByPreparationAndCookingTime(
			@PathVariable("startPreparation") int startPreparation, @PathVariable("endPreparation") int endPreparation,
			@PathVariable("startCooking") int startCooking, @PathVariable("endCooking") int endCooking) {
		List<RecipeDTO> recipesRetrieved = recipeService.retrieveByPreparationAndCookingTime(startPreparation,
				endPreparation, startCooking, endCooking);
		return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
	}

	@GetMapping("/retrieveByCalories/{min}/{max}")
	public ResponseEntity<List<RecipeDTO>> retrieveByNbrCaloriesBetween(@PathVariable("min") int min,
																		@PathVariable("max") int max) {
		List<RecipeDTO> recipesRetrieved = recipeService.retrieveByCaloriesBetween(min, max);
		return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
	}
	@GetMapping("/retrieveAll/{pageNo}/{pageSize}") //if you want to retrieve all recipes with pagination just put 0 as a value for pageNo and pageSize
	public ResponseEntity<List<RecipeCardDTO>> retrieve(@PathVariable int pageNo, @PathVariable int pageSize) {
		List<RecipeCardDTO> recipesRetrieved = recipeService.getAllRecipesWithPagination(pageNo, pageSize);
		return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
	}
	@GetMapping("/retrieveOnlyAcceptedOrVerifiedRecipes/{pageNo}/{pageSize}")
	public ResponseEntity<List<RecipeCardDTO>> retrieveOnlyAcceptedOrVerifiedRecipes(@PathVariable int pageNo, @PathVariable int pageSize) {
		List<RecipeCardDTO> recipesRetrieved = recipeService.getOnlyVerifiedOrAcceptedRecipesWithPagination(pageNo, pageSize);
		return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
	}
	@GetMapping("/retrieveAllCreatorRecipes/{pageNo}/{pageSize}") //if you want to retrieve all recipes with pagination just put 0 as a value for pageNo and pageSize
	public ResponseEntity<List<RecipeCardDTO>> retrieveCreatorRecipes(@PathVariable int pageNo, @PathVariable int pageSize, @RequestHeader("AUTHORIZATION") String header) {
		String username = userService.getusernamefromtoken(header);

		List<RecipeCardDTO> recipesRetrieved = recipeService.getAllCreatorRecipesWithPagination(pageNo, pageSize,username);
		return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
	}

	@GetMapping("/getCreatorProfileWithVerifiedAndAcceptedRecipes/{pageNo}/{pageSize}/{idCreator}")
	public ResponseEntity<CreatorDTOForProfile> getCreatorProfileWithVerifiedAndAcceptedRecipes(@RequestHeader("AUTHORIZATION") String header,
																					  @PathVariable int pageNo, @PathVariable int pageSize,
																					  @PathVariable("idCreator") Long idCreator) {
		String username = userService.getusernamefromtoken(header);

		CreatorDTOForProfile profile = recipeService.getCreatorProfileWithVerifiedAndAcceptedRecipes(pageNo, pageSize,username,idCreator);
		return new ResponseEntity<>(profile, HttpStatus.OK);
	}

	@GetMapping("/retrieveVerifiedAndAcceptedCreatorRecipes/{pageNo}/{pageSize}/{idCreator}")
	public ResponseEntity<List<RecipeCardDTO>> retrieveVerifiedAndAcceptedCreatorRecipes(@PathVariable int pageNo, @PathVariable int pageSize,
																						 @PathVariable("idCreator") Long idCreator) {

		List<RecipeCardDTO> recipesRetrieved = recipeService.getVerifiedAndAcceptedRecipes(pageNo, pageSize,idCreator);
		return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
	}
	@GetMapping("/retrieveAll")
	public ResponseEntity<List<RecipeDTO>> retrieveAll() {
		List<RecipeDTO> recipesRetrieved = recipeService.retrieveAll();
		return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
	}

	@PostMapping(value = "/addRecipeToMeal/{idMeal}/{idRecipe}")
	public void addRecipeToMeal(@PathVariable ("idMeal") Long idMeal, @PathVariable("idRecipe") List<Long> idRecipe){
		recipeService.addRecipeToMeal(idMeal, idRecipe);
	}
	@DeleteMapping(value = "/deleteRecipeFromMeal/{idMeal}/{idRecipe}")
	public void deleteRecipeFromMeal(@PathVariable Long idMeal, @PathVariable("idRecipe")List<Long> idRecipe){
		recipeService.deleteRecipeFromMeal(idMeal, idRecipe);
	}

	@PostMapping(value = "/addRecipeToUser/{idFavoris}")
	public void addFavoriteRecipeToUser(@RequestHeader("AUTHORIZATION") String header, @PathVariable List<Long> idFavoris){
		String username = userService.getusernamefromtoken(header);
		recipeService.addFavoriteRecipeToUser(username,idFavoris);
	}

	@DeleteMapping(value = "/deleteRecipeFromUser/{idFavoris}")
	public void deleteFavoriteRecipeFromUser(@RequestHeader("AUTHORIZATION") String header, @PathVariable List<Long> idFavoris){
		String username = userService.getusernamefromtoken(header);
		recipeService.deleteFavoriteRecipeFromUser(username,idFavoris);
	}


	@GetMapping(value = "getAllFavoriteRecipe/{page}/{size}")
	public ResponseEntity<List<RecipeCardDTO>> findAllByFavoris(@RequestHeader("AUTHORIZATION") String header, @PathVariable int page, @PathVariable int size) {
		String username = userService.getusernamefromtoken(header);
		if (page == 0 && size == 0)

		{
			List<RecipeCardDTO> recipeCardDTOS = recipeService.getFavorite(username, Pageable.unpaged());
			return ResponseEntity.ok(recipeCardDTOS);
		}

		Pageable pageable = PageRequest.of(page -1, size);
		List<RecipeCardDTO> recipeCardDTOS = recipeService.getFavorite(username, pageable);
		return ResponseEntity.ok(recipeCardDTOS);
	}

	@GetMapping("searchRecipeByQuery")
	public ResponseEntity<List<RecipeCardDTO>> searchRecipe(@RequestParam("query") String query){
		return ResponseEntity.ok(recipeService.searchRecipe(query));
	}

	@PostMapping(value = "addVideoToRecipe/{idRecipe}/{idVideo}")
	public void addVideoToRecipe(@PathVariable ("idRecipe") Long idRecipe, @PathVariable ("idVideo") Long idVideo){
		recipeService.addVideoToRecipe(idRecipe, idVideo);
	}

	@GetMapping("searchRecipefromfavorites")
	public ResponseEntity<List<RecipeCardDTO>> searchRecipefromfavorites(@RequestParam("query") String query , @RequestHeader("AUTHORIZATION") String header){
		String username = userService.getusernamefromtoken(header);
		return ResponseEntity.ok(recipeService.searchRecipefromfavorites(query ,username));
	}


	@GetMapping("/getReceipesByMealTag/{mealTitle}")
	public List<RecipeCardDTO> get3RecipeSuggestionsByMealTagOrRandom(@RequestHeader("AUTHORIZATION") String header,@PathVariable String mealTitle) {
		String username = userService.getusernamefromtoken(header);
		return recipeService.get3RecipeSuggestionsByMealTagOrRandom(mealTitle,username);
	}
	@PutMapping("/addVideotoStep/{idStep}")
	@Transactional
	public ResponseEntity add(@Valid @PathVariable Long idStep, @RequestParam MultipartFile video){
		Video savedVideo = fileLocationService.saveVideo(video);
		if(savedVideo==null){
			return new ResponseEntity<>("video not saved", HttpStatus.NOT_ACCEPTABLE);
		}
		boolean videoSaved= recipeService.addVideoToStep(idStep,savedVideo);
		if (videoSaved)
			return new ResponseEntity<>("video saved", HttpStatus.CREATED);
		else
			return new ResponseEntity<>("error occurred" ,HttpStatus.NOT_ACCEPTABLE);
	}
	@PostMapping("/addv2")

	public ResponseEntity<String> addv2(@Valid @RequestPart Recipe recipe,
										@RequestParam MultipartFile image, @RequestParam MultipartFile video,@RequestParam Map<String, MultipartFile> selectedVideos,@RequestHeader("AUTHORIZATION") String header) throws Exception {
		String username = userService.getusernamefromtoken(header);
		Creator creator = userService.findByUsernameCreator(username);

		if (creator == null) {
			return new ResponseEntity<>("Creator not found", HttpStatus.NOT_FOUND);
		}

		recipe.setCreator(creator);

		if(recipeService.existsByName(recipe.getName())){
			return new ResponseEntity<>("recipe name exists", HttpStatus.NOT_ACCEPTABLE);
		}

		Image savedimage = fileLocationService.save(image);
		if(savedimage==null){
			return new ResponseEntity<>("image not saved", HttpStatus.NOT_ACCEPTABLE);
		}

		Video video1 = fileLocationService.saveVideo(video);
		if(video1==null){
			return new ResponseEntity<>("couldn't save video",HttpStatus.NOT_ACCEPTABLE);
		}
		String msg = recipeService.addv2(recipe,savedimage,video1,selectedVideos,username);

		if (Objects.equals(msg, "saved")){
			return new ResponseEntity<>(msg, HttpStatus.CREATED);


		}
		else
			return new ResponseEntity<>(msg ,HttpStatus.NOT_ACCEPTABLE);
	}


	@GetMapping("/findByTimeAndTags")
	public List<RecipeCardDTO> findByTimeAndTags(@RequestParam int time, @RequestParam Set<Long> tagIds) {
		Set<Tag> tags = tagService.findByIdIn(tagIds);
		return recipeService.findByTimeAndTags(time, tags);
	}

	@PostMapping("/incrementView/{recipeId}")
	public ResponseEntity<Boolean> incrementViews(@PathVariable Long recipeId){
		boolean result = recipeService.incrementViews(recipeId);
		if (result) {
			return new ResponseEntity<>(true, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
		}
	}
}

