package spark.ukla.services.interfaces;

import javassist.NotFoundException;
import spark.ukla.entities.Meal;

import java.util.List;
import java.util.Optional;

public interface IMealService {


    Optional<Meal> getMeal(Long mealId);
    Meal updateMealName(Meal meal) throws NotFoundException;
    String deleteMeal (Long mealId);

    Meal addMealToDay(Long idDay,String mealName);
    Boolean addMealToPlan(Long idPlan,String mealName);

    Meal editMealName(Long id, String mealName);

}