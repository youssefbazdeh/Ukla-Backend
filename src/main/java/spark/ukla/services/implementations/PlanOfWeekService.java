package spark.ukla.services.implementations;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.DTO.PlanDTO;
import spark.ukla.converters.PlanConverter;
import spark.ukla.entities.Day;
import spark.ukla.entities.Meal;
import spark.ukla.entities.PlanOfWeek;
import spark.ukla.entities.User;
import spark.ukla.entities.bodyinfos.FemaleBodyInfo;
import spark.ukla.entities.bodyinfos.MaleBodyInfo;
import spark.ukla.entities.enums.Gender;
import spark.ukla.entities.groceyList.GroceryDay;
import spark.ukla.entities.groceyList.GroceryIngredientQuantityObject;
import spark.ukla.entities.groceyList.GroceryList;
import spark.ukla.entities.groceyList.GroceryRecipe;
import spark.ukla.repositories.*;
import spark.ukla.repositories.projection.PlanOfWeekProjection;
import spark.ukla.services.interfaces.GroceryListRepository;
import spark.ukla.services.interfaces.IPlanOfWeek;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanOfWeekService implements IPlanOfWeek {



    private final PlanOfWeekRepository planOfWeekRepository;

    private final UserRepository userRepository;
    private final NutritionService nutritionService ;
    private final PlanConverter planConverter;
    private final GroceryDayRepository groceryDayRepository;
    private final GroceryListRepository groceryListRepository;
    private final GroceryRecipeRepository groceryRecipeRepository;
    private final GroceryIngredientQuantityObjectRepository groceryIngredientQuantityObjectRepository;
    @Autowired
    public PlanOfWeekService(PlanOfWeekRepository planOfWeekRepository, UserRepository userRepository, NutritionService nutritionService, PlanConverter planConverter, GroceryDayRepository groceryDayRepository, GroceryListRepository groceryListRepository, GroceryRecipeRepository groceryRecipeRepository, GroceryIngredientQuantityObjectRepository groceryIngredientQuantityObjectRepository) {

        this.planOfWeekRepository = planOfWeekRepository;
        this.userRepository = userRepository;
        this.nutritionService = nutritionService;
        this.planConverter = planConverter;
        this.groceryDayRepository = groceryDayRepository;
        this.groceryListRepository = groceryListRepository;
        this.groceryRecipeRepository = groceryRecipeRepository;
        this.groceryIngredientQuantityObjectRepository = groceryIngredientQuantityObjectRepository;
    }

    @Override
    public PlanOfWeek addPlan(LocalDate date,String username) {

        PlanOfWeek newPlan = new PlanOfWeek();
        List<Day> daysList = new ArrayList<>();
        for (int j =0; j <7; j++) {
            Day day = new Day() ;
            Meal breakfastMeal = new Meal();
            breakfastMeal.setName("Breakfast");
            breakfastMeal.setDay(day);

            Meal launchMeal = new Meal();
            launchMeal.setName("Lunch");
            launchMeal.setDay(day);

            Meal dinnerMeal = new Meal();
            dinnerMeal.setName("Dinner");
            dinnerMeal.setDay(day);

            List<Meal> meals = new ArrayList<>();
            meals.add(breakfastMeal);
            meals.add(launchMeal);
            meals.add(dinnerMeal);

            day.setMeals(meals);
            day.setPlanOfWeek(newPlan);
            day.setDate(date.plusDays(j));
            day.setName(date.plusDays(j).getDayOfWeek().toString());
            daysList.add(day);
        }

        newPlan.setName("New plan");
        newPlan.setDays(daysList);
        User currentUser = userRepository.findByUsername(username) ;
        newPlan.setUser(currentUser);
        setPlanCalories(newPlan,currentUser);
       // newPlan.setCalories(currentUser.get);
        if(currentUser.getFollowedPlanId()== null){ // this means that this is the first plan, and it will be followed by default
            newPlan.setFollowed(true);
            PlanOfWeek savedPlan = planOfWeekRepository.save(newPlan);
            userRepository.followPlan(savedPlan.getId(),username);
            return  savedPlan ;

        }

        return planOfWeekRepository.save(newPlan);
    }


    @Override
    public boolean deleteById(Long id) {
        PlanOfWeek plan = planOfWeekRepository.findById(id).orElse(null);
        if(plan!=null) {
            planOfWeekRepository.deleteById(id);
            if(plan.getFollowed()) {
                User user = userRepository.findByFollowedPlanId(id);
                deleteGroceryDaysAndAllAssociations(id);
                user.setFollowedPlanId(null);
            }
            return true ;
        }
        else
            return  false;
    }


    public List<Long> extractGroceryDayIds(Long id) {
        GroceryList groceryList = groceryListRepository.findByPlanOfWeekId(id);
        List<Long> groceryDayIds = new ArrayList<>();
        if (groceryList!= null && groceryList.getGroceryDays()!= null) {
            for (GroceryDay groceryDay : groceryList.getGroceryDays()) {
                groceryDayIds.add(groceryDay.getId());
            }
        }
        return groceryDayIds;
    }

    @Transactional
    @Override
    public String update(PlanOfWeek planOfWeek) {
        String msg = "";
        boolean Exists = planOfWeekRepository.existsById(planOfWeek.getId());
        if(Exists){
            planOfWeekRepository.update(planOfWeek.getId(),planOfWeek.getName());
            msg = "updated";
        }else
            msg = "not apdated";
        return msg;
    }


    @Override
    public List<PlanOfWeekProjection> retrieveAllBYUser(String username, Pageable pageable) {
            Page<PlanOfWeekProjection> plans = planOfWeekRepository.findAllByUser_Username(username, pageable);
            return plans.getContent();
    }

    @Override
    public PlanOfWeek retrieveByName(String name) {
        boolean ExistsByName = planOfWeekRepository.existsByName(name);
        if(ExistsByName)
            return  planOfWeekRepository.findByName(name);
        else
            return null;
    }


    public void deleteGroceryDaysAndAllAssociations(Long idPlan){
        List<Long> groceryRecipeID=new ArrayList<>();
        List<Long> groceryIngredientQuantityObjectID=new ArrayList<>();
        List<Long> ids = extractGroceryDayIds(idPlan);
        groceryListRepository.deletGroceryListGroceryDayById(ids);
        for(Long groceryDayId : ids){
            GroceryDay groceryDayToDelete = groceryDayRepository.getById(groceryDayId);
            if(!groceryDayToDelete.getRecipes().isEmpty()){
                for(GroceryRecipe groceryRecipeToDelete : groceryDayToDelete.getRecipes()){
                    if(!groceryRecipeToDelete.getGroceryIngredientQuantityObjects().isEmpty()){
                        for(GroceryIngredientQuantityObject groceryIngredientQuantityObjectToDelete : groceryRecipeToDelete.getGroceryIngredientQuantityObjects()){
                            groceryIngredientQuantityObjectID.add(groceryIngredientQuantityObjectToDelete.getId());
                        }
                    }
                    groceryRecipeID.add(groceryRecipeToDelete.getId());
                }
            }
        }
        groceryIngredientQuantityObjectRepository.deleteGroceryIngredientQuantityObjectsInAssociationTableByIds(groceryIngredientQuantityObjectID);
        groceryIngredientQuantityObjectRepository.deleteGroceryIngredientQuantityObjectsByIds(groceryIngredientQuantityObjectID);

        groceryRecipeRepository.deleteGroceryRecipesInAssociationTableByIds(groceryRecipeID);
        groceryRecipeRepository.deleteGroceryRecipesByIds(groceryRecipeID);

        groceryDayRepository.deleteGroceryDayInAssociationGroceryDayRecipesByIds(ids);
        groceryDayRepository.deleteGroceryDaysByIds(ids);
    }

    @Override

    public Boolean followPlan(Long idPlan,String username) {
       if(!planOfWeekRepository.existsById(idPlan))  {
           return false ;
       }
       
        Long currentlyFollowedPlanId =userRepository.getfollowedPlan(username) ;
        if(currentlyFollowedPlanId!=null){
            deleteGroceryDaysAndAllAssociations(currentlyFollowedPlanId);
        }
        planOfWeekRepository.updateFollowed(currentlyFollowedPlanId,false) ;
        planOfWeekRepository.updateFollowed(idPlan,true) ;
       userRepository.followPlan(idPlan,username);

       return true ;
       }


    public PlanOfWeek retrieveById(Long idPlan) {
       PlanOfWeek plan =  planOfWeekRepository.findById(idPlan).get();
        User user = plan.getUser() ;

         setPlanCalories(plan, user);

        return plan ;

    }


    private void setPlanCalories(PlanOfWeek plan, User user) {
        // if user is a female get userinfo from femaleBodyInfo  first
        if(user.getGender().equals(Gender.Female)) {
            FemaleBodyInfo femaleBodyInfo =  nutritionService.getFemaleBodyInfoByUser(user) ;
            if(femaleBodyInfo!= null){
                plan.setCalories(femaleBodyInfo.getCalorieNeed());
            }
            else{
                MaleBodyInfo maleBodyInfo =  nutritionService.getMaleBodyInfoByUser(user) ;
                if(maleBodyInfo==null){
                    plan.setCalories(0);

                }
                else
                plan.setCalories(maleBodyInfo.getCalorieNeed());
            }
        }
        // if user is a male get userinfo from maleBodyInfo  first
        else {
             MaleBodyInfo maleBodyInfo =  nutritionService.getMaleBodyInfoByUser(user) ;
             if(maleBodyInfo!=null){
                 plan.setCalories(maleBodyInfo.getCalorieNeed());
             }
             else{
                 FemaleBodyInfo femaleBodyInfo =  nutritionService.getFemaleBodyInfoByUser(user) ;
                 if(femaleBodyInfo== null){
                     plan.setCalories(0);

                 }
                 else
                 plan.setCalories(femaleBodyInfo.getCalorieNeed());

             }

        }

    }

    @Override
    public String renamePlan(String newPlanName, long id) {

            PlanOfWeek planOfWeek = planOfWeekRepository.findById(id).get();
             planOfWeekRepository.renamePLan(planOfWeek.getId(), newPlanName);
            return "plan renamed";

    }
    public List<PlanDTO> getFollowedPlan(String username) {
        Long id = userRepository.getfollowedPlan(username);
        if(id == null){
            return Collections.emptyList();
        }else{
            PlanOfWeek followedPlan=planOfWeekRepository.findById(id).get();
            User user = followedPlan.getUser();
            setPlanCalories(followedPlan,user);
            List<PlanOfWeek> followedPlanList = new ArrayList<>();
            followedPlanList.add(followedPlan);
            return planConverter.entitesToDTO(followedPlanList);
        }
    }
    public boolean changeDateOfPlan(Long planId,LocalDate newStartDate) {
        PlanOfWeek followedPlan = planOfWeekRepository.findById(planId).orElse(null);

        if (followedPlan == null) {
            return false;
        }

        LocalDate currentStartDate = followedPlan.getDays().get(0).getDate();
        List<Day> days = followedPlan.getDays();


        // Calculate the difference in days between the new start date and the current start date
        long daysDifference = ChronoUnit.DAYS.between(currentStartDate, newStartDate);

        for (Day day : days) {
            LocalDate originalDate = day.getDate();
            LocalDate updatedDate = originalDate.plusDays(daysDifference);
            day.setDate(updatedDate);

            // Set the name of the day based on the day of the week of the updated date
            DayOfWeek dayOfWeek = updatedDate.getDayOfWeek();
            String newDayName = dayOfWeek.name();
            day.setName(newDayName);
        }

        planOfWeekRepository.save(followedPlan);
        return true;
    }
}