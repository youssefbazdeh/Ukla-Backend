package spark.ukla.foodIntakeEstimation.EstimationIngredient;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.entities.Image;
import spark.ukla.services.implementations.FileLocationService;
import spark.ukla.services.implementations.UserService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/EstimationIngredient")
public class EstimationIngredientController {
    private final EstimationIngredientService estimationIngredientService;
    private final FileLocationService fileLocationService ;
    private final EstimationIngredientRepository estimationIngredientRepository;
    private final UserService userService;

    public EstimationIngredientController(EstimationIngredientService estimationIngredientService, FileLocationService fileLocationService,
                                          EstimationIngredientRepository estimationIngredientRepository, UserService userService) {
        this.estimationIngredientService = estimationIngredientService;
        this.fileLocationService = fileLocationService;
        this.estimationIngredientRepository = estimationIngredientRepository;
        this.userService = userService;
    }
    @PostMapping("/add")
    @Transactional
    public ResponseEntity add(@Valid @RequestPart EstimationIngredient estimationIngredient,
                              @RequestParam MultipartFile image) throws Exception {
        if(estimationIngredientRepository.findByName(estimationIngredient.getName())!=null){
            return new ResponseEntity<>("EstimationIngredient already exist", HttpStatus.NOT_ACCEPTABLE);
        }
        else{
        Image savedimage = fileLocationService.save(image);
        if(savedimage==null){
            return new ResponseEntity<>("EstimationIngredient image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        Boolean EstimationIngredientSaved= estimationIngredientService.add(estimationIngredient,savedimage);
        if (EstimationIngredientSaved)
            return new ResponseEntity<>("EstimationIngredient saved", HttpStatus.CREATED);
        else
            return new ResponseEntity<>("error occurred" ,HttpStatus.NOT_ACCEPTABLE);}
    }
    @PutMapping("/update")
    ResponseEntity update(@RequestBody EstimationIngredient estimationIngredient) throws NotFoundException {
        estimationIngredientService.update(estimationIngredient);
        return ResponseEntity.ok("EstimationIngredient updated");
    }
    @GetMapping("/All")
    List<EstimationIngredient> getAll(){
        return estimationIngredientService.getAll();
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity deleteProducts(@PathVariable("id") Long Id){

        return ResponseEntity.ok(estimationIngredientService.delete(Id));
    }
    @GetMapping("/search/{name}")
    ResponseEntity<List<EstimationIngredient>> getByName(@PathVariable("name") String name){
        return ResponseEntity.ok(estimationIngredientService.SearchByName(name));
    }
}

