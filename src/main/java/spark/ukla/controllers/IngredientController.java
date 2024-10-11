package spark.ukla.controllers;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.DTO.IngredientDTO;
import spark.ukla.azurebob.AzureBlobFileService;
import spark.ukla.entities.Image;
import spark.ukla.entities.Ingredient;
import spark.ukla.repositories.IngredientRepository;
import spark.ukla.services.implementations.FileLocationService;
import spark.ukla.services.implementations.IngredientService;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/ingredient")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientController {
    @Autowired
    IngredientRepository ingredientRepository;
    private final IngredientService ingredientService;
    private final FileLocationService fileLocationService ;

    public IngredientController(IngredientService ingredientService, AzureBlobFileService azureBlobFileService, FileLocationService fileLocationService) {
        this.ingredientService = ingredientService;
        this.fileLocationService = fileLocationService;
    }
    @GetMapping("/getAll")

    public ResponseEntity<Iterable<Ingredient>> getAllIngredient() {


        return new ResponseEntity<>(ingredientService.getAll(), HttpStatus.OK);

    }
    @GetMapping("/retrieveAll/{pageNo}/{pageSize}")
    public ResponseEntity<List<Ingredient>> retrieve(@PathVariable int pageNo, @PathVariable int pageSize) {
        List<Ingredient> recipesRetrieved = ingredientService.getAllIngredientsWithPagination(pageNo, pageSize);
        return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
    }

    @GetMapping("/retrieveAlltranslated/{pageNo}/{pageSize}/{languageCode}")
    public ResponseEntity<List<?>> retrieve(@PathVariable int pageNo, @PathVariable int pageSize,
                                                     @PathVariable("languageCode") String languageCode) {
        List<?> recipesRetrieved = ingredientService.getAllTranslatedIngredientsWithPagination(pageNo, pageSize,languageCode);
        return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
    }
    @GetMapping("/getById/{id}")
    public ResponseEntity<Ingredient> retrieveById(@PathVariable Long id){
        Ingredient ingredient = ingredientService.retrieveById(id);
        if(ingredient != null)
            return new ResponseEntity(ingredient, HttpStatus.OK);
        else
            return new ResponseEntity(ingredient, HttpStatus.NOT_FOUND);
    }
    @PostMapping("/import")
    public ResponseEntity<List<Ingredient>> importIngredientsFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            List<Ingredient> ingredients = ingredientService.importIngredientsFromExcel(file);
            return ResponseEntity.ok(ingredients);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
    @Transactional
    public ResponseEntity<String> add(@Valid @RequestPart Ingredient ingredient,
                                      @RequestParam MultipartFile image) throws Exception {

        if(ingredientService.nameExists(ingredient.getName()))
            return new ResponseEntity<>("ingredient name exists", HttpStatus.NOT_ACCEPTABLE);

        Image savedimage = fileLocationService.save(image);
        if(savedimage==null){
            return new ResponseEntity<>("ingredient image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        Boolean IngredientSaved = ingredientService.add(ingredient,savedimage);
        if (IngredientSaved)
            return new ResponseEntity<>("ingredient saved", HttpStatus.CREATED);
        else
            return new ResponseEntity<>("error occurred" ,HttpStatus.NOT_ACCEPTABLE);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getIngredientCount() {
        long count = ingredientService.getRecipeCount();
        return ResponseEntity.ok(count);
    }
    @PutMapping("/update")
    @Transactional
    public ResponseEntity<String> update(@Valid @RequestPart Ingredient ingredient) throws Exception {
        if ((ingredientService.update(ingredient,ingredientRepository.findById(ingredient.getId()).get().getImage())))
        return new ResponseEntity<>("ingredient updated", HttpStatus.OK);
    else
        return new ResponseEntity<>("error occurred" ,HttpStatus.NOT_ACCEPTABLE);
}






    @PutMapping ("/updateimage/{ingredientId}")
    @Transactional
    public ResponseEntity<String> updateImage(@PathVariable Long ingredientId,
                                              @RequestParam MultipartFile image) throws Exception {



        Image savedimage = fileLocationService.save(image);
        if(savedimage==null){
            return new ResponseEntity<>("ingredient image not saved", HttpStatus.NOT_ACCEPTABLE);
        }


        String msg = ingredientService.updateImaage(ingredientId,savedimage);
        if (msg.equals("ingredient saved"))
            return new ResponseEntity<>(msg, HttpStatus.OK);
        else
            return new ResponseEntity<>(msg ,HttpStatus.NOT_ACCEPTABLE);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        ingredientService.deleteById(id);
        return new ResponseEntity<>("deleted",HttpStatus.OK);
    }


    @GetMapping("searchTanslatedIngredient/{languageCode}")
    public ResponseEntity<List<IngredientDTO>> searchTanslatedIngredient(@RequestParam("query") String query,
                                                                         @PathVariable("languageCode") String languageCode){
        return ResponseEntity.ok(ingredientService.searchTanslatedIngredient(query,languageCode));
    }

    @GetMapping("searchIngredientByQuery")
    public ResponseEntity<List<Ingredient>> searchIngredient(@RequestParam("query") String query){
        return ResponseEntity.ok(ingredientService.searchIngredient(query));
    }



}
