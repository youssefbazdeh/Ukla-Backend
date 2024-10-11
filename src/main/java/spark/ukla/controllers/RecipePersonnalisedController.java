package spark.ukla.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.DTO.RecipePersonnalisedDTO;
import spark.ukla.converters.RecipePersonnalisedConverter;
import spark.ukla.entities.Image;
import spark.ukla.entities.RecipePersonnalised;
import spark.ukla.repositories.RecipePersonnalisedRepository;
import spark.ukla.services.implementations.FileLocationService;
import spark.ukla.services.implementations.UserService;
import spark.ukla.services.interfaces.IRecipePersonnalised;
import spark.ukla.utils.FileUpload;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Slf4j
@RestController
@RequestMapping("/RecipePersonnalised")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipePersonnalisedController {

    @Autowired
    IRecipePersonnalised iRecipePersonnalised;
    @Autowired
    RecipePersonnalisedRepository repository;

    @Autowired
    RecipePersonnalisedConverter converter;

    @Autowired
    UserService userService;

    @Autowired
    FileLocationService fileLocationService;

    @PostMapping(value = "/add",  consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<String> add(@Valid @RequestPart ("personalisedDto") RecipePersonnalisedDTO personalisedDto, @RequestPart("image") MultipartFile image) throws Exception {

        Image image1 = fileLocationService.save(image);
        if(image1==null){
            return new ResponseEntity<>("image not saved", HttpStatus.NOT_ACCEPTABLE);
        }

        String msg = iRecipePersonnalised.add(personalisedDto,image1);
        if (Objects.equals(msg, "recipe saved"))
            return new ResponseEntity<>(msg, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(msg, HttpStatus.NOT_ACCEPTABLE);
    }

    @RequestMapping(value = "get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RecipePersonnalised retrieveById(@PathVariable Long id){
        return  iRecipePersonnalised.retrieveById(id);
    }

    @RequestMapping(value = "retrieveAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<RecipePersonnalisedDTO>> retrieveAll(){
        List<RecipePersonnalisedDTO> recipeRetrieved = iRecipePersonnalised.retrieveAll();
        return new ResponseEntity<>(recipeRetrieved, HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    String deletePlan(@PathVariable Long id){
        return  iRecipePersonnalised.deleteById(id);
    }


    // compress the image bytes before storing it in the database
    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
        }
        System.out.println("Compressed Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }
    // uncompress the image bytes before returning it to the angular application
    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException ioe) {
        } catch (DataFormatException e) {
        }
        return outputStream.toByteArray();
    }

    @PutMapping(value = "/update",  consumes = "multipart/form-data")
    public ResponseEntity<RecipePersonnalisedDTO> updateRecipe(@RequestPart RecipePersonnalisedDTO personalisedDto,
                         @RequestParam("images") MultipartFile[] multipartFile) throws IOException {
        byte[] images = new byte[0];
        //personalisedDto.setImages(images);
        for (MultipartFile file : multipartFile) {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            //personalisedDto.setImages(images);
            String uploadDir = "recipe-photos/" + personalisedDto.getName();
            FileUpload.saveFile(uploadDir, fileName, file);
        }
        RecipePersonnalisedDTO recipeUpdated = iRecipePersonnalised.update(personalisedDto);
        if (recipeUpdated != null)
            return new ResponseEntity<>(recipeUpdated, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @GetMapping("retrieveByToAvoid")
    public ResponseEntity<List<RecipePersonnalisedDTO>> retrieveByToAvoid(@RequestBody RecipePersonnalisedDTO recipePersonnalisedDTO){
        List<RecipePersonnalisedDTO> recipe = iRecipePersonnalised
                .retrieveByToAvoid(converter.dtoToEntity(recipePersonnalisedDTO).getToAvoid());
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @GetMapping("retrieveByToRecommend")
    public ResponseEntity<List<RecipePersonnalisedDTO>> retrieveByToRecommend(@RequestBody RecipePersonnalisedDTO recipePersonnalisedDTO){
        List<RecipePersonnalisedDTO> recipe = iRecipePersonnalised
                .retrieveByToRecommend(converter.dtoToEntity(recipePersonnalisedDTO).getToRecommend());
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @GetMapping("/retrieveByNbrCalories/{min}/{max}")
    public ResponseEntity<List<RecipePersonnalisedDTO>> retrieveByNbrCaloriesBetween(@PathVariable int min,@PathVariable int max) {
        List<RecipePersonnalisedDTO> recipe = iRecipePersonnalised
                .retrieveByNbrCaloriesBetween(min,max);
        return new ResponseEntity<>(recipe, HttpStatus.OK);
    }

    @GetMapping("/retrieveByPreparationAndCookingTime/{startPreparation}/{endPreparation}/{startCooking}/{endCooking}")
    public ResponseEntity<List<RecipePersonnalisedDTO>> retrieveByPreparationAndCookingTime(
           @PathVariable("startPreparation") int startPreparation, @PathVariable("endPreparation") int endPreparation,
           @PathVariable("startCooking") int startCooking, @PathVariable("endCooking") int endCooking) {

        List<RecipePersonnalisedDTO> recipesRetrieved = iRecipePersonnalised.retrieveByPreparationAndCookingTime(startPreparation,
                endPreparation, startCooking, endCooking);
        return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
    }

    @GetMapping("/retrieveByCookingTime/{start}/{end}")
    public ResponseEntity<List<RecipePersonnalisedDTO>> retrieveByCookingTime(@PathVariable("start") int start,
           @PathVariable("end") int end) {

        List<RecipePersonnalisedDTO> recipesRetrieved = iRecipePersonnalised.retrieveByCookingTime(start, end);
        return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);

    }

    @GetMapping("/retrieveByPreparationTime/{start}/{end}")
    public ResponseEntity<List<RecipePersonnalisedDTO>> retrieveByPreparationTime(@PathVariable("start") int start,
           @PathVariable("end") int end) {

        List<RecipePersonnalisedDTO> recipesRetrieved = iRecipePersonnalised.retrieveByPreparationTime(start, end);
        return new ResponseEntity<>(recipesRetrieved, HttpStatus.OK);
    }

    @PostMapping(value = "/addRecipePersonalisedToUser/{idFavorisPerson}")
    public void addFavoriteRecipePersonalisedToUser(@RequestHeader("AUTHORIZATION") String header, @PathVariable List<Long> idFavorisPerson){
        String username = userService.getusernamefromtoken(header);
        iRecipePersonnalised.addFavoriteRecipePersonalisedToUser(username,idFavorisPerson);
    }

    @DeleteMapping(value = "deleteRecipePersonalisedToUser/{idFavorisPerson}")
    public void deleteFavoriteRecipePersonalisedFromUser(@RequestHeader("AUTHORIZATION") String header, @PathVariable List<Long> idFavorisPerson){
        String username = userService.getusernamefromtoken(header);
        iRecipePersonnalised.deleteFavoriteRecipePersonalisedFromUser(username,idFavorisPerson);
    }

    @PutMapping("/updateImage/{id}")
    public ResponseEntity<String> updateImage(@RequestParam MultipartFile image, @PathVariable Long id) throws Exception {
        Image image1 = fileLocationService.save(image);
        if(image1 == null){
            return new ResponseEntity<>("image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        String msg = iRecipePersonnalised.updateImage(image1,id);
        return new ResponseEntity<>(msg,HttpStatus.OK);
    }

    @GetMapping(value = "getAllFavoriteRecipePersonalised")
    public List<RecipePersonnalised> findAllByFavoris(@RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);
        return  repository.getFavorite(username);
    }

    @GetMapping(value = "searchForRecipePersonalisedByName")
    public List<RecipePersonnalised> searchRecipePersonalised(@RequestParam String query){
        return iRecipePersonnalised.searchRecipePersonalised(query);
    }
}