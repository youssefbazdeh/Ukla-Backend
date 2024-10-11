package spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.entities.Image;
import spark.ukla.services.implementations.FileLocationService;

import java.util.List;


@RestController
@RequestMapping("/EstimationIngredientQuantity")
public class EstimationIngredientQuantityController {
    private final EstimationIngredientQuantityService estimationIngredientQuantityService;
    private final FileLocationService fileLocationService;

    public EstimationIngredientQuantityController(EstimationIngredientQuantityService estimationIngredientQuantityService, FileLocationService fileLocationService) {
        this.estimationIngredientQuantityService = estimationIngredientQuantityService;
        this.fileLocationService = fileLocationService;
    }

    @PostMapping("/add/{idEstimationIngredient}")
    @Transactional
    public ResponseEntity<String> add(@PathVariable("idEstimationIngredient") Long idEstimationIngredient
            ,@RequestPart EstimationIngredientQuantity estimationIngredientQuantity
            ,@RequestParam("image") MultipartFile image) throws Exception {
        Image savedimage = fileLocationService.save(image);
        if(savedimage==null){
            return new ResponseEntity<>("EstimationIngredientQuantity image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        boolean estimationIngredientQuantitySaved=estimationIngredientQuantityService.add(idEstimationIngredient,estimationIngredientQuantity, savedimage) ;
        if (estimationIngredientQuantitySaved)
            return new ResponseEntity<>("EstimationIngredientQuantity saved", HttpStatus.CREATED);
        else
            return new ResponseEntity<>("error occurred" ,HttpStatus.NOT_ACCEPTABLE);
    }
    @PutMapping("/update")
    ResponseEntity update(@RequestBody EstimationIngredientQuantity estimationIngredientQuantity) throws NotFoundException {
        return ResponseEntity.ok(estimationIngredientQuantityService.update(estimationIngredientQuantity));
    }
    @GetMapping("/All")
    List<EstimationIngredientQuantity> getAll(@RequestParam("idEstimationIngredient") Long idEstimationIngredient){
        return estimationIngredientQuantityService.getAll(idEstimationIngredient);
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity delete(@PathVariable("id") Long Id){
        return ResponseEntity.ok(estimationIngredientQuantityService.delete(Id));
    }
}
