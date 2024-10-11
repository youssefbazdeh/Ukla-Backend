package spark.ukla.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import spark.ukla.services.implementations.DislikesService;
import spark.ukla.services.implementations.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/Dislikes")
public class DislikesController {
    private final DislikesService dislikesService;
    private final UserService userService;

    private DislikesController(DislikesService dislikesService, UserService userService) {
        this.dislikesService = dislikesService;
        this.userService = userService;
    }
    @PostMapping("/addAll")

    public ResponseEntity addAll(@Valid @RequestBody List<Long> ingredientIds, @RequestHeader("AUTHORIZATION") String header) throws Exception {
        String username = userService.getusernamefromtoken(header);
        dislikesService.addDislikedIngredients(username,ingredientIds);
        return new ResponseEntity<>("Dislikes saved", HttpStatus.CREATED);
    }
    @GetMapping("/getAll")
    public ResponseEntity getAll(@RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);
        return new ResponseEntity<>(dislikesService.getAllDislikedIngredients(username), HttpStatus.OK);
    }
    @DeleteMapping("/delete/{ingredientId}")

    public ResponseEntity delete(@PathVariable Long ingredientId, @RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);
        dislikesService.removeDislikedIngredient(username,ingredientId);
        return new ResponseEntity<>("Dislike deleted", HttpStatus.OK);
    }

}
