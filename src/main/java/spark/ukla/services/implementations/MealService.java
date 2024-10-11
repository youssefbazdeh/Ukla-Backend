package spark.ukla.services.implementations;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.Day;
import spark.ukla.entities.Meal;
import spark.ukla.entities.PlanOfWeek;
import spark.ukla.repositories.DayRepository;
import spark.ukla.repositories.MealRepository;
import spark.ukla.repositories.PlanOfWeekRepository;
import spark.ukla.repositories.RecipeRepository;
import spark.ukla.services.interfaces.IMealService;
import java.util.List;
import java.util.Optional;

@Service
public class MealService implements IMealService {

    @Autowired
    MealRepository mealRepository;


    @Autowired
    DayRepository dayRepository;
    @Autowired
    PlanOfWeekRepository planOfWeekRepository;
    @Autowired
    RecipeRepository recipeRepository;



    @Override
    public Optional<Meal> getMeal(Long mealId) {
        Boolean ExistsById = mealRepository.existsById(mealId);
        if(ExistsById)
        return mealRepository.findById(mealId);
        else
            return null;
    }

    @Override
    public Meal updateMealName(Meal meal) throws NotFoundException {

        if(meal == null || meal.getId() == null){
            throw new NotFoundException("Meal or ID must not be null! ");
        }
        Optional<Meal> optionalMeal = mealRepository.findById(meal.getId());
        if(!optionalMeal.isPresent()){
            throw new NotFoundException("Meal with ID: " + meal.getId() + " does not exist. ");
        }
        Meal existingMeal = optionalMeal.get();
        existingMeal.setName(meal.getName());
         mealRepository.updateName(meal.getId(), meal.getName());
        return existingMeal ;

    }

    @Override
    public String deleteMeal(Long mealId) {
        String msg = "";
        Optional<Meal> optionalMeal =  mealRepository.findById(mealId);
        if(optionalMeal.isPresent()){

            mealRepository.deleteById(mealId);
        }else
            msg = "Meal ID not found.";
        return msg;
    }

    @Override
    public Meal addMealToDay(Long idDay,String mealName) {
        Meal meal = new Meal();
        Day day = dayRepository.findById(idDay).orElse(null);
        System.out.println(mealName);
        meal.setDay(day);

        meal.setName(mealName);

        System.out.println(meal.getName());
        return mealRepository.save(meal);
    }

    @Override
    @Transactional
    public Boolean addMealToPlan(Long idPlan,String mealName) {
        Optional<PlanOfWeek> plan = Optional.ofNullable(planOfWeekRepository.findById(idPlan).orElse(null));
        if(!plan.isPresent())
            return false ;
        List<Day> planDays = plan.get().getDays();

        for(Day day: planDays){

            Meal meal1 = new Meal();
            meal1.setDay(day);

            meal1.setName(mealName);
            mealRepository.save(meal1);
        }
        return true ;
    }

    @Override
    public Meal editMealName(Long id, String mealName) {
        Meal meal = mealRepository.findById(id).get();
        meal.setName(mealName);
        return mealRepository.save(meal);
    }
}