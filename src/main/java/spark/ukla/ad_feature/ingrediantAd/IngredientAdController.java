package spark.ukla.ad_feature.ingrediantAd;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.entities.Image;
import spark.ukla.services.implementations.FileLocationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/ingredientAd")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientAdController {
    private final IngredientAdService ingredientAdService;
    private final FileLocationService fileLocationService;

    public IngredientAdController(IngredientAdService ingredientAdService,FileLocationService fileLocationService){
        this.ingredientAdService=ingredientAdService;
        this.fileLocationService=fileLocationService;
    }

    @PostMapping(value = "/add/{id}/{ingrediantname}")
    public ResponseEntity<String> add(@Valid @RequestPart("ingredientAd") IngredientAd ingredientAd,
                                                  @PathVariable("ingrediantname") String ingrediantname,
                                                  @PathVariable("id") Long id,
                                                  @RequestParam MultipartFile image)throws Exception{
        Image savedimage = fileLocationService.save(image);
        if(savedimage==null){
            return new ResponseEntity<>("image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        if (ingredientAdService.addIngredientAdAndSetIngredientId(ingredientAd,ingrediantname,savedimage,id)!=null){
            return new ResponseEntity<>("Created", HttpStatus.CREATED);

        }
        return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get/{pageNo}/{pageSize}/{idCampaign}")
    public ResponseEntity<List<IngredientAd>> getIngredientAd(@PathVariable int pageNo,
                                                              @PathVariable int pageSize,
                                                              @PathVariable Long idCampaign) {
        List<IngredientAd> IngredientAdsWithPagination = ingredientAdService.getIngredientAdsWithPagination(pageNo,pageSize,idCampaign);
        if (IngredientAdsWithPagination!= null &&!IngredientAdsWithPagination.isEmpty()) {
            return new ResponseEntity<>(IngredientAdsWithPagination, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> retrieveById(@PathVariable Long id){
        IngredientAd ingredientAd = ingredientAdService.retrieveById(id);
        if(ingredientAd != null)
            return new ResponseEntity<>(ingredientAd, HttpStatus.OK);
        else
            return new ResponseEntity<>("ingredient Ad does not exist", HttpStatus.NOT_FOUND);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getIngredientAdCount() {
        long count = ingredientAdService.getCount();
        return ResponseEntity.ok(count);
    }


    @PutMapping("desable/{id}")
    public ResponseEntity<String> desable(@PathVariable("id") Long idIngredientAd) {
        if (ingredientAdService.disable(idIngredientAd)) {
            return new ResponseEntity<>("updated", HttpStatus.OK);

        } else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        ingredientAdService.deleteByid(id);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }

    @PutMapping("/updateImage/{id}")
    public ResponseEntity<String> updateImage(@RequestParam MultipartFile image, @PathVariable Long id) throws Exception {
        Image image1 = fileLocationService.save(image);
        if (image1 == null) {
            return new ResponseEntity<>("image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        String msg = ingredientAdService.updateImage(image1, id);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }
    @PutMapping("update/{id}/{ingrediantname}")
    public ResponseEntity<String> update(@Valid @RequestPart("ingAd") IngredientAd ingredientAd,
                                         @PathVariable Long id,
                                         @PathVariable("ingrediantname") String ingrediantname) {

        if (ingredientAdService.updateAd(ingredientAd,id,ingrediantname)) {
            return new ResponseEntity<>("updated", HttpStatus.OK);

        } else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);

    }


    @GetMapping("/getByIngredientIdsAndActive/{countryCode}")
    public ResponseEntity<List<IngredientAd>> getAllByIngredientIdsAndActiveAndActive(@RequestParam("ids") List<Long> ids,@PathVariable("countryCode") String countryCode){
        if(ids.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        CountryCode code = CountryCode.valueOf(countryCode);
        List<IngredientAd> ingredientAds = ingredientAdService.getAllByIngredientIdsAndActiveAndCountryCode(ids,code);
        if(ingredientAds.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(ingredientAds,HttpStatus.OK);
    }

}
