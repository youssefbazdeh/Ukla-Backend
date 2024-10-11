package spark.ukla.controllers;

import javassist.NotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.entities.Meal;
import spark.ukla.services.interfaces.IMealService;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/Meal")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MealController {

    @Autowired
    IMealService iMealService;

    @GetMapping(value = "getById/{mealId}")
    public Optional<Meal> getMeal(@PathVariable(value = "mealId") Long mealId){
        return iMealService.getMeal(mealId);
    }

    @PutMapping(value = "/updateMealName")
    Meal updateMeal(@RequestBody Meal meal) throws NotFoundException {
        return iMealService.updateMealName(meal);
    }
    @DeleteMapping(value = "deleteByID/{mealId}")
    public String deleteMeal (@PathVariable(value = "mealId") Long mealId){
        return iMealService.deleteMeal(mealId);
    }
    @PostMapping("/addMealToDay/{idDay}")
    public Meal addMealToDay(@PathVariable (value = "idDay") Long idDay,@RequestBody  String mealName){
        return iMealService.addMealToDay(idDay,mealName);
    }
    @PostMapping("/addMealToPlan/{idPlan}")
    public ResponseEntity addMealToPlan(@PathVariable (value = "idPlan") Long idPlan,@RequestBody  String mealName){
          Boolean  result =   iMealService.addMealToPlan(idPlan,mealName);
          if (result== false)
          return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("no plan found matching that id") ;

          return  ResponseEntity.status(HttpStatus.OK).body("meal added to plan") ;
    }

    @PutMapping(value = "/editMealName/{idMeal}")
    Meal editMealName(@PathVariable (value = "idMeal") Long idMeal,@RequestBody String mealName) throws NotFoundException {
        return iMealService.editMealName(idMeal,mealName);
    }
}


