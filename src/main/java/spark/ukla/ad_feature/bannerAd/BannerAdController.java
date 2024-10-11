package spark.ukla.ad_feature.bannerAd;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.entities.Image;
import spark.ukla.entities.Video;
import spark.ukla.services.implementations.FileLocationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/bannerAd")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerAdController {
    private final BannerAdService bannerAdService;
    private final FileLocationService fileLocationService;

    public BannerAdController(BannerAdService bannerAdService,FileLocationService fileLocationService){
        this.bannerAdService=bannerAdService;
        this.fileLocationService=fileLocationService;
    }

    @PostMapping(value = "/add/{id}")
    public ResponseEntity<String> add(@Valid @RequestPart BannerAd bannerAd,
                                              @RequestParam(required = false) MultipartFile video,
                                              @RequestParam MultipartFile image,
                                              @PathVariable("id") Long id)throws Exception{
        Image savedImage = fileLocationService.save(image);
        if(savedImage==null){
            return new ResponseEntity<>("image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        if (video!= null &&!video.isEmpty()) {
            Video savedVideo = fileLocationService.saveVideo(video);
            if (savedVideo == null) {
                return new ResponseEntity<>("couldn't save video", HttpStatus.NOT_ACCEPTABLE);
            }
            bannerAdService.add(bannerAd,savedVideo,savedImage,id);
            return new ResponseEntity<>("Created", HttpStatus.CREATED);
        }
        if (video==null){
            bannerAdService.add(bannerAd,null,savedImage,id);
            return new ResponseEntity<>("Created", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);


    }
    @GetMapping("/get/{pageNo}/{pageSize}/{idCampaign}")
    public ResponseEntity<List<BannerAd>> get(@PathVariable int pageNo,
                                                          @PathVariable int pageSize,
                                                          @PathVariable Long idCampaign) {
        List<BannerAd> BannerAdsWithPagination = bannerAdService.getBannerAdsWithPagination(pageNo,pageSize,idCampaign);
        if (BannerAdsWithPagination!= null &&!BannerAdsWithPagination.isEmpty()) {
            return new ResponseEntity<>(BannerAdsWithPagination, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> retrieveById(@PathVariable Long id){
        BannerAd bannerAd = bannerAdService.retrieveById(id);
        if(bannerAd != null)
            return new ResponseEntity<>(bannerAd, HttpStatus.OK);
        else
            return new ResponseEntity<>("Banner Ad does not exist", HttpStatus.NOT_FOUND);
    }
    @GetMapping("/count")
    public ResponseEntity<Long> getBannerAdCount() {
        long count = bannerAdService.getBannerAdCount();
        return ResponseEntity.ok(count);
    }

    @PutMapping("disable/{id}")
    public ResponseEntity<String> disable(@PathVariable("id") Long idBanner) {
        if (bannerAdService.disable(idBanner)) {
            return new ResponseEntity<>("updated", HttpStatus.OK);

        } else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        bannerAdService.deleteById(id);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }
    @PutMapping("/updateImage/{id}")
    public ResponseEntity<String> updateImage(@RequestParam("image") MultipartFile image, @PathVariable Long id) throws Exception {
        Image savedImage = fileLocationService.save(image);
        if (savedImage == null) {
            return new ResponseEntity<>("image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        String msg = bannerAdService.updateImage(savedImage, id);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }
    @PutMapping("/updateVideo/{id}")
    public ResponseEntity<String> updateVideo(@RequestParam("video") MultipartFile video, @PathVariable Long id) {
        Video saveVideo = fileLocationService.saveVideo(video);
        if(saveVideo == null) {
            return new ResponseEntity<>("video not found", HttpStatus.NOT_FOUND);
        }
        String msg = bannerAdService.updateVideo(saveVideo,id);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }
    @PutMapping("update/{id}")
    public ResponseEntity<String> update(@Valid @RequestPart("bannerAd") BannerAd bannerAd,
                                         @PathVariable Long id) {

        if (bannerAdService.updateAd(bannerAd,id)) {
            return new ResponseEntity<>("updated", HttpStatus.OK);

        } else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);

    }
    @PutMapping("/activate/{bannerAdId}")
    public ResponseEntity<String> activate(@PathVariable Long bannerAdId) {

        if (bannerAdService.activate(bannerAdId)) {
            return new ResponseEntity<>("activated", HttpStatus.OK);

        } else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);

    }
    @PutMapping("setVideoToNull/{id}")
    public ResponseEntity<String> setBannerVideoToNull(@PathVariable Long id){
        if (bannerAdService.setBannerVideoToNull(id)) {
            return new ResponseEntity<>("updated", HttpStatus.OK);

        } else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);
    }
    @GetMapping("getAll/{countryCode}")
    public ResponseEntity<?> getActiveBannerAdsByCountryCode(@PathVariable("countryCode")String countryCode){
        CountryCode code = CountryCode.valueOf(countryCode);
        List<Long> activeBannerAdsByCountryCode = bannerAdService.findAllByCountryCodeAndActive(code);
        if(activeBannerAdsByCountryCode.isEmpty()){
            return new ResponseEntity<>("no banner ad found",HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(activeBannerAdsByCountryCode,HttpStatus.OK);
    }
}
