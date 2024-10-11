package spark.ukla.foodIntakeEstimation.EstimationMeal;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import spark.ukla.services.implementations.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/EstimationMeal")
public class EstimationMealController {
    private final EstimationMealService estimationMealService;
    private final UserService userService;

    public EstimationMealController(EstimationMealService estimationMealService, UserService userService) {
        this.estimationMealService = estimationMealService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity<List<EstimationMeal>> addMultipleItems(@RequestHeader("AUTHORIZATION") String header,@RequestBody List<EstimationMeal> Meals) {
        String username = userService.getusernamefromtoken(header);
        for (EstimationMeal Meal :Meals) {
            estimationMealService.add(Meal,username);
        }
        return ResponseEntity.ok(Meals);
    }
    @PutMapping("/update/{id}/{Name}")
    ResponseEntity updateName(@PathVariable("id") Long Id, @PathVariable("Name") String name){
        return ResponseEntity.ok(estimationMealService.updatename(Id,name));
    }
    @GetMapping("/get/{id}")
    Optional<EstimationMeal> getOne(@PathVariable("id") Long Id){
        return estimationMealService.getOne(Id);
    }

    @GetMapping("/All")
    List<EstimationMeal> getAllByUserId(@RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);

        return estimationMealService.getAllByUserId(username);
    }
    @DeleteMapping("/delete/{id}")
    ResponseEntity delete(@PathVariable("id") Long Id){
       return ResponseEntity.ok(estimationMealService.delete(Id));
    }

    @DeleteMapping("/deleteEstimationRecipe/{idMeal}/{idrecipe}")
    ResponseEntity deleteEstimationRecipe(@PathVariable("idMeal") Long IdMeal,@PathVariable("idrecipe") Long estimationRecipe){
        estimationMealService.removeEstimationRecipe(IdMeal,estimationRecipe);
        if (estimationMealService.removeEstimationRecipe(IdMeal,estimationRecipe))
        return ResponseEntity.ok("Estimation Recipe deleted");
        else return ResponseEntity.notFound().build();
    }
}
