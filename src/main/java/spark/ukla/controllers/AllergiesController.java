package spark.ukla.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.entities.Allergy;
import spark.ukla.entities.Image;
import spark.ukla.repositories.AllergiesRepository;
import spark.ukla.services.implementations.AllergiesService;
import spark.ukla.services.implementations.FileLocationService;
import spark.ukla.services.implementations.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/Allergies")
public class AllergiesController {
    private final AllergiesService allergiesService;
    private final UserService userService;
    private final FileLocationService fileLocationService;
    @Autowired
    AllergiesRepository allergyRepository;
    private AllergiesController(AllergiesService allergiesService, UserService userService, FileLocationService fileLocationService) {
        this.allergiesService = allergiesService;
        this.userService = userService;
        this.fileLocationService = fileLocationService;
    }
    @PostMapping("/add")
    public ResponseEntity<String> add(@Valid @RequestPart String name, @RequestParam List<Long> IngredientsIds,
                                      @RequestParam MultipartFile image) throws Exception {

        if(allergiesService.existsByName(name)){
            return new ResponseEntity<>("allergy name exists", HttpStatus.FOUND);
        }
        Image savedImage = fileLocationService.save(image);
        if(savedImage==null){
            return new ResponseEntity<>("image not saved", HttpStatus.NO_CONTENT);
        }
        if (allergiesService.add(name,IngredientsIds,savedImage))
            return new ResponseEntity<>("Allergy saved", HttpStatus.CREATED);
        else
            return new ResponseEntity<>("error occurred" ,HttpStatus.NOT_ACCEPTABLE);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Allergy> retrieveById(@PathVariable Long id){
        Allergy allergy = allergiesService.retrieveById(id);
        if(allergy != null)
            return new ResponseEntity<>(allergy, HttpStatus.OK);
        else
            return new ResponseEntity<>(allergy, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/addForUser")
    public ResponseEntity addForUser(@RequestHeader("AUTHORIZATION") String header, @RequestBody List<Long> ids){
        String username = userService.getusernamefromtoken(header);
        if (allergiesService.addAllergiesIds(ids,username))
            return new ResponseEntity<>("Allergies saved", HttpStatus.CREATED);
        else
            return new ResponseEntity<>("error occurred" ,HttpStatus.NOT_ACCEPTABLE);
    }
    @GetMapping("/All")
    public List<Allergy> getAll(){
        return allergiesService.getAll();
    }
    @GetMapping("/AllByUser")
    public List<Allergy> getAllByUserId(@RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);
        return allergiesService.getAllByUserId(username);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id){
        if(allergiesService.delete(id))
            return new ResponseEntity<>("Allergy deleted", HttpStatus.OK);
        else
            return new ResponseEntity<>("error occurred" ,HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@Valid @RequestPart Allergy allergy, @RequestParam List<Long> IngredientsIds
    ) throws Exception {


        if (allergiesService.update(allergy, IngredientsIds,allergyRepository.findById(allergy.getId()).get().getImage()))
            return new ResponseEntity<>("Allergy updated", HttpStatus.OK);
        else
            return new ResponseEntity<>("error occurred", HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/updateImage/{allergyId}")
    public ResponseEntity<String> updateImage(@PathVariable Long allergyId, @RequestParam MultipartFile image) throws Exception {


        Image savedimage = fileLocationService.save(image);
        if (savedimage == null) {
            return new ResponseEntity<>("allergy image not saved", HttpStatus.NOT_ACCEPTABLE);
        }


        if (allergiesService.updateImage(savedimage, allergyId))
            return new ResponseEntity<>("image updated", HttpStatus.OK);
        else
            return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);
    }

    @DeleteMapping("/deleteAllergiesByUserId/{id}")
    public ResponseEntity deleteAllergyByUserId(@PathVariable("id") Long id, @RequestHeader("AUTHORIZATION") String header) {
        String username = userService.getusernamefromtoken(header);
        if (allergiesService.deleteAllergiesByUserId(id, username))
            return new ResponseEntity<>("Allergy deleted", HttpStatus.OK);
        else
            return new ResponseEntity<>("error occurred", HttpStatus.NOT_ACCEPTABLE);
    }
}
