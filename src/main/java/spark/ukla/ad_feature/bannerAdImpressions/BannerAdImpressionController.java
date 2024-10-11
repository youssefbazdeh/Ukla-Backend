package spark.ukla.ad_feature.bannerAdImpressions;

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
@RequestMapping("/bannerAdImpression")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerAdImpressionController {
    private final BannerAdImpressionService bannerAdImpressionService;
    private final UserService userService;

    public BannerAdImpressionController(BannerAdImpressionService bannerAdImpressionService, UserService userService){
        this.bannerAdImpressionService = bannerAdImpressionService;
        this.userService = userService;

    }


    @PostMapping("/incrementActions/{idbanner}")
    public ResponseEntity<Boolean> incrementActions(@PathVariable Long idbanner,
                                                    @RequestPart BannerMetricsIncrementor actions,
                                                    @RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);
        boolean result = bannerAdImpressionService.incrementBannerAdImpressionMetrics(idbanner,username,actions);
        if (result) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getBannerAdStatsByCampaignId/{id}")
    public ResponseEntity<List<BannerAdStat>> retrieveById(@PathVariable Long id){
        List<BannerAdStat> bannerAdStats = bannerAdImpressionService.getBannerAdStats(id);
        if(bannerAdStats != null)
            return new ResponseEntity<>(bannerAdStats, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/getFiltredBannerAdStatsByCampaignId/{id}/{filter}")
    public ResponseEntity<List<BannerAdStat>> retrieveById(@PathVariable Long id,@PathVariable String filter){
        List<BannerAdStat> bannerAdStats = bannerAdImpressionService.getFilteredBannerAdStats(id,filter);
        if(bannerAdStats != null)
            return new ResponseEntity<>(bannerAdStats, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
