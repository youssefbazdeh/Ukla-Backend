package spark.ukla.services.implementations;

import org.springframework.stereotype.Service;
import spark.ukla.converters.GroceryListConverter;
import spark.ukla.entities.*;
import spark.ukla.entities.groceyList.GroceryDay;
import spark.ukla.entities.groceyList.GroceryRecipe;
import spark.ukla.entities.groceyList.GroceryIngredientQuantityObject;
import spark.ukla.entities.groceyList.GroceryList;
import spark.ukla.repositories.*;
import spark.ukla.services.interfaces.GroceryListRepository;
import spark.ukla.services.interfaces.IGroceryListService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GroceryListService implements IGroceryListService {
    private final  UserRepository userRepository ;
    private final DayRepository dayRepository;
    private final PlanOfWeekRepository planOfWeekRepository;
    private final GroceryListRepository groceryListRepository;
    private final GroceryIngredientQuantityObjectRepository groceryIngredientQuantityObjectRepository ;
    private final GroceryRecipeRepository groceryRecipeRepository;
    private final GroceryListConverter groceryListConverter;

    public GroceryListService(UserRepository userRepository, DayRepository dayRepository, PlanOfWeekRepository planOfWeekRepository, GroceryListRepository groceryListRepository, GroceryIngredientQuantityObjectRepository groceryIngredientQuantityObjectRepository, GroceryRecipeRepository groceryRecipeRepository,GroceryListConverter groceryListConverter) {
        this.userRepository = userRepository;
        this.dayRepository = dayRepository;
        this.planOfWeekRepository = planOfWeekRepository;
        this.groceryListRepository = groceryListRepository;
        this.groceryIngredientQuantityObjectRepository = groceryIngredientQuantityObjectRepository;
        this.groceryRecipeRepository = groceryRecipeRepository;
        this.groceryListConverter = groceryListConverter;
    }

    @Override
    public GroceryList retrieveGroceryList(String username,String languageCode,String countryCode){
      Long followedPlanId =  userRepository.getfollowedPlan(username) ;
      GroceryList groceryList = groceryListRepository.getByUsername(username) ;
        if (followedPlanId!=null){
            if(groceryList==null){ // there is no grocery list
                GroceryList newGroceryList = new GroceryList();
                newGroceryList.setUser(userRepository.findByUsername(username));
                newGroceryList.setPlanOfWeekId(followedPlanId);
                return groceryListConverter.entityToTranslated(getGroceryListDto( getContentThenSaveGroceryList(newGroceryList ,followedPlanId)),languageCode,countryCode);
            }
            else if(!Objects.equals(followedPlanId, groceryList.getPlanOfWeekId())){ // the user followed another plan
                GroceryList newGroceryList = new GroceryList();
                newGroceryList.setId(groceryList.getId());
                newGroceryList.setUser(groceryList.getUser());
                newGroceryList.setPlanOfWeekId(followedPlanId);
                return groceryListConverter.entityToTranslated(getGroceryListDto( getContentThenSaveGroceryList(newGroceryList,followedPlanId)),languageCode,countryCode);
            }
            checkForUpdates(followedPlanId, groceryList);
            return groceryListConverter.entityToTranslated(getGroceryListDto(groceryList),languageCode,countryCode);
        }
        else return null;
    }

    @Override

    public void checkForUpdates(Long followedPlanId, GroceryList groceryList) {
        // Get the days associated with the followed plan
        PlanOfWeek followedPlanOfWeek = planOfWeekRepository.getById(followedPlanId);
        List<Day> planDays = followedPlanOfWeek.getDays();
        // Check for new recipes and add them to the GroceryList
        for (Day planDay : planDays) {
            for (Meal meal : planDay.getMeals()) {
                for (Recipe recipe : meal.getRecipes()) {
                    for (GroceryDay groceryDay : groceryList.getGroceryDays()) {
                        int difference = calculateRecipeOccurrenceDifference(recipe.getId(), planDay, groceryDay);
                        if (groceryDay.getDayId().equals(planDay.getId())) {
                            boolean groceryRecipeExistsInGroceryList = groceryDay.getRecipes().stream()
                                    .anyMatch(groceryRecipe -> groceryRecipe.getRecipe().getId().equals(recipe.getId()));

                            if (!groceryRecipeExistsInGroceryList || difference > 0) {
                                for (int i = 0; i < difference; i++) {
                                    groceryDay.getRecipes().add(createGroceryRecipeFromRecipe(recipe));
                                }
                            }
                        }
                    }
                }
            }
        }
        // toDo check if the groceryingredientquantityobjects are deleted
        // Check for removed recipes and delete them from the GroceryList
        for (GroceryDay groceryDay : groceryList.getGroceryDays()) {
            List<GroceryRecipe> recipesToRemove = new ArrayList<>();
            for (GroceryRecipe groceryRecipe : groceryDay.getRecipes()) {
                Long recipeId = groceryRecipe.getRecipe().getId();
                boolean recipeExistsInPlan = false;
                for (Day planDay : planDays) {
                    for (Meal meal : planDay.getMeals()) {
                        for (Recipe recipe : meal.getRecipes()) {
                            if (recipe.getId().equals(recipeId)) {
                                recipeExistsInPlan = true;
                                break;
                            }
                        }
                        if (recipeExistsInPlan) {
                            break;
                        }
                    }
                    if (recipeExistsInPlan) {
                        break;
                    }
                }
                if (!recipeExistsInPlan) {
                    recipesToRemove.add(groceryRecipe);
                    groceryRecipeRepository.delete(groceryRecipe);
                }
            }
            groceryDay.getRecipes().removeAll(recipesToRemove);
        }
        // Save the updated GroceryList
        groceryListRepository.save(groceryList);
    }

    private GroceryRecipe createGroceryRecipeFromRecipe(Recipe recipe) {
        GroceryRecipe groceryRecipe = new GroceryRecipe() ;
        groceryRecipe.setRecipe(recipe);


        List<GroceryIngredientQuantityObject> groceryIngredientQuantityObjectList = new ArrayList<>();
        for (Step step : recipe.getSteps()){
            for (IngredientQuantityObject recipeIngredientQuantityObject: step.getIngredientQuantityObjects()) {

                GroceryIngredientQuantityObject groceryIngredientQuantityObject = new GroceryIngredientQuantityObject();
                groceryIngredientQuantityObject.setIngredientQuantityObject(recipeIngredientQuantityObject);
                groceryIngredientQuantityObjectList.add(groceryIngredientQuantityObjectRepository.save(groceryIngredientQuantityObject)) ;

            }
        }


        groceryRecipe.setGroceryIngredientQuantityObjects(groceryIngredientQuantityObjectList);
        return groceryRecipe;
    }

    @Override
    public GroceryList getGroceryListDto(GroceryList groceryList) {  // this methode is made to not return the recipes in days without the meals
        for (GroceryDay groceryDay: groceryList.getGroceryDays()) {
            String dayName =dayRepository.findNameById(groceryDay.getDayId());
            groceryDay.setName(dayName);
            for (GroceryRecipe groceryRecipe: groceryDay.getRecipes()
                 ) {
                groceryRecipe.setImage(groceryRecipe.getRecipe().getImage());
                groceryRecipe.setName(groceryRecipe.getRecipe().getName());

            }

        }
        return groceryList;

    }

    @Override
    public void deleteGroceryIngredientQuantityObject(List<Long> IdsList) {
       groceryIngredientQuantityObjectRepository.deleteGroceryIngredientQuantityObjectsInAssociationTableByIds(IdsList);
        groceryIngredientQuantityObjectRepository.deleteAllById(IdsList);
    }

    @Override
    public void purchase(List<Long> IdsList) {
        groceryIngredientQuantityObjectRepository.purchase(IdsList);
    }

    @Override
    public void unpurchase(List<Long> IdsList) {
        groceryIngredientQuantityObjectRepository.unpurchase(IdsList);
    }


    public GroceryList getContentThenSaveGroceryList(GroceryList groceryList, Long followedPlanId) {

        List<GroceryDay> groceryDays = new ArrayList<>();
        PlanOfWeek followedPlanOfWeek = planOfWeekRepository.getById(followedPlanId);
        List<Day> planOfWeekDays = followedPlanOfWeek.getDays();
        for (Day day: planOfWeekDays) {
            GroceryDay groceryDay = new GroceryDay() ;
            groceryDay.setDayId(day.getId());

            List<GroceryRecipe> groceryRecipeList = new ArrayList<>();

            for (Meal meal:day.getMeals()) {
                for (Recipe recipe:meal.getRecipes()
                ) {


                    groceryRecipeList.add(createGroceryRecipeFromRecipe(recipe)) ;
                }

            }
            groceryDay.setRecipes(groceryRecipeList);
            groceryDays.add(groceryDay) ;
        }

        groceryList.setGroceryDays(groceryDays);
       return groceryListRepository.save(groceryList) ;




    }

    public int countRecipeOccurrencesInDay(Long recipeId, Day day) {
        int occurrences = 0;
        for (Meal meal : day.getMeals()) {
            for (Recipe recipe : meal.getRecipes()) {
                if (recipe.getId().equals(recipeId)) {
                    occurrences++;
                }
            }
        }
        return occurrences;
    }

    public int countGroceryRecipeOccurrencesInAGroceryDay(Long recipeId, GroceryDay groceryDay) {
        int occurrences = 0;
        for (GroceryRecipe groceryRecipe : groceryDay.getRecipes()) {
            if (groceryRecipe.getRecipe().getId().equals(recipeId)) {
                occurrences++;
            }
        }
        return occurrences;
    }

    public int calculateRecipeOccurrenceDifference(Long recipeId, Day day, GroceryDay groceryday) {
        int dayOccurrences = countRecipeOccurrencesInDay(recipeId, day);
        int groceryListOccurrences = countGroceryRecipeOccurrencesInAGroceryDay(recipeId, groceryday);
        return dayOccurrences - groceryListOccurrences;
    }
}