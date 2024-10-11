package spark.ukla.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.entities.groceyList.GroceryList;
import spark.ukla.services.implementations.GroceryListService;
import spark.ukla.services.implementations.UserService;

import java.util.List;

@RestController
@RequestMapping("/grocery-list")
public class groceryListController {

   private final UserService userService;
   private final GroceryListService groceryListService ;

    public groceryListController(UserService userService, GroceryListService groceryListService) {
        this.userService = userService;
        this.groceryListService = groceryListService;
    }

    @GetMapping("/{languageCode}/{countryCode}")
    ResponseEntity<?> getGroceryList(@RequestHeader("AUTHORIZATION") String header,@PathVariable("languageCode") String languageCode,@PathVariable("countryCode") String countryCode){
        String username = userService.getusernamefromtoken(header);
        try {
            GroceryList groceryList =  groceryListService.retrieveGroceryList(username,languageCode,countryCode);
            if (groceryList == null){
                return new ResponseEntity<>("No grocery list found for the user.", HttpStatus.NOT_FOUND);
            }else {
                return new ResponseEntity<>(groceryList, HttpStatus.OK);
            }
        } catch (Exception e){
            return new ResponseEntity<>("An error occurred while retrieving the grocery list.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{ids}")
    ResponseEntity<String> deleteGroceryIngredientQuantityObject(@PathVariable("ids") List<Long> GroceryIngredientQuantityObjectIds){
        groceryListService.deleteGroceryIngredientQuantityObject(GroceryIngredientQuantityObjectIds);
        return new ResponseEntity<>("items deleted", HttpStatus.OK) ;

    }
    @PutMapping("/purchased/{id}")
    ResponseEntity<String> purchasedGroceryIngredientQuantityObject(@PathVariable("id") List<Long> GroceryIngredientQuantityObjectIds){
        groceryListService.purchase(GroceryIngredientQuantityObjectIds);
        return new ResponseEntity<>("items purchased", HttpStatus.OK) ;

    }
    @PutMapping("/unpurchase/{id}")
    ResponseEntity<String> unpurchaseGroceryIngredientQuantityObject(@PathVariable("id") List<Long> GroceryIngredientQuantityObjectIds){
        groceryListService.unpurchase(GroceryIngredientQuantityObjectIds);
        return new ResponseEntity<>("items unpurchased", HttpStatus.OK) ;

    }
}
