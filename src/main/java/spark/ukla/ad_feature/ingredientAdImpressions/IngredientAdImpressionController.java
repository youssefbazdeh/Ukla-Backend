package spark.ukla.ad_feature.ingredientAdImpressions;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.services.implementations.UserService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/ingredientAdImpression")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientAdImpressionController {
    private final IngredientAdImpressionService ingredientAdImpressionService;
    private final UserService userService;

    public IngredientAdImpressionController(IngredientAdImpressionService ingredientAdImpressionService, UserService userService){
        this.ingredientAdImpressionService = ingredientAdImpressionService;
        this.userService = userService;

    }


    @PostMapping("/incrementView")
    public ResponseEntity<Boolean> incrementViews(@RequestParam List<Long> ids,
                                                  @RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);

        boolean result = ingredientAdImpressionService.incrementViews(ids,username);
        if (result) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("/incrementImpression")
    public ResponseEntity<Boolean> incrementImpressions(@RequestParam List<Long> ids,
                                                  @RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);

        boolean result = ingredientAdImpressionService.incrementImpressions(ids,username);
        if (result) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getIngredientAdStatsByCampaignId/{id}")
    public ResponseEntity<List<IngredientAdStat>> retrieveById(@PathVariable Long id){
        List<IngredientAdStat> ingredientAdStats = ingredientAdImpressionService.getIngredientAdStats(id);
        if(ingredientAdStats != null)
            return new ResponseEntity<>(ingredientAdStats, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
