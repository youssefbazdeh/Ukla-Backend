package spark.ukla.foodIntakeEstimation.EstimationRecipe;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/EstimationRecipe")
public class EstimationRecipeController {
    private final EstimationRecipeService estimationRecipeService;

    public EstimationRecipeController(EstimationRecipeService estimationRecipeService) {
        this.estimationRecipeService = estimationRecipeService;
    }


    @PostMapping("/add/{idEstMeal}")
    public ResponseEntity add(@RequestBody EstimationRecipe estimationRecipe, @PathVariable("idEstMeal") Long idEstimationMeal) throws Exception {
        estimationRecipeService.add(estimationRecipe, idEstimationMeal);
        return new ResponseEntity<>("EstimationRecipe saved", HttpStatus.CREATED);
    }
    @PutMapping("/update")
    ResponseEntity update(@RequestBody EstimationRecipe estimationRecipe) throws NotFoundException {
        return ResponseEntity.ok(estimationRecipeService.update(estimationRecipe));
    }
    @GetMapping("/All")
    List<EstimationRecipe> getAll(){
        return estimationRecipeService.getAll();
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity deleteProducts(@PathVariable("id") Long Id){
        return ResponseEntity.ok(estimationRecipeService.delete(Id));
    }

    @PutMapping("/addQuantity/{id}")
    ResponseEntity addListQuantity(@PathVariable("id") Long Id,@RequestBody List<Long> estimationQuantityIds){
        return ResponseEntity.ok(estimationRecipeService.addListQuantity(Id,estimationQuantityIds));
    }
    @PutMapping("/removeQuantity/{id}/{QuantityId}")
    ResponseEntity removeListQuantity(@PathVariable("id") Long estimationRecipeId,@PathVariable("QuantityId") Long quantityId){
        return ResponseEntity.ok(estimationRecipeService.deleteQuantity(estimationRecipeId,quantityId));

    }
}

