package spark.ukla.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.services.implementations.FavorisService;
import spark.ukla.services.implementations.UserService;

@RestController
@RequestMapping("/Favoris")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FavorisController {


	private final FavorisService favorisService;

	private final UserService userService;

    public FavorisController(FavorisService favorisService, UserService userService) {
        this.favorisService = favorisService;
        this.userService = userService;
    }

    @PutMapping("/add/{recipe-id}")
	@ResponseBody
	public ResponseEntity<String> add(@RequestHeader("AUTHORIZATION") String header, @PathVariable("recipe-id") Long recipeId) {
		String username = userService.getusernamefromtoken(header);
		String msg = favorisService.add(username, recipeId);
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
		
	}
	
	@DeleteMapping("/delete/{recipe-id}")
	@ResponseBody
	public ResponseEntity<String> delete(@RequestHeader("AUTHORIZATION") String header, @PathVariable("recipe-id") Long recipeId) {
		String username = userService.getusernamefromtoken(header);
		String msg = favorisService.delete(username, recipeId);
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
		
	}

}


