package spark.ukla.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.entities.Recipe;
import spark.ukla.entities.waitList;
import spark.ukla.services.implementations.RecipeService;
import spark.ukla.services.implementations.waitService;

import javax.validation.Valid;

@RestController
@RequestMapping("/wait")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class waitController {
    @Autowired
   waitService waitServices;
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/add")

    public ResponseEntity<String> add(@RequestBody waitList wait) throws Exception {

        if(waitServices.existsByName(wait.getEmail())){
            return new ResponseEntity<>("Already subscribed", HttpStatus.FOUND);
        }
    else{
            waitServices.add(wait);
            return new ResponseEntity<>("OK", HttpStatus.OK);

        }

    }
}
