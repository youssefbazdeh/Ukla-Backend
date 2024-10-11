package spark.ukla.ad_feature.campaign;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/Campaign")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignController {
    private final CampaignService campaignService;

    public CampaignController(CampaignService campaignService){
    this.campaignService=campaignService;
    }

    @PostMapping(value = "/addCompaign/{name}")
    public ResponseEntity<String> addCompaign(@Valid @RequestPart("campaign") Campaign campaign,
                                              @PathVariable("name") String name ){
        if (campaignService.addCampaign(campaign,name)) {
            return new ResponseEntity<>("Created", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>( HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getCompaigns/{pageNo}/{pageSize}")
    public ResponseEntity<List<Campaign>> getCompaigns(@PathVariable int pageNo,
                                                   @PathVariable int pageSize) {
        List<Campaign> compaignsWithPagination = campaignService.getCampaignsWithPagination(pageNo,pageSize);
        if (compaignsWithPagination!= null &&!compaignsWithPagination.isEmpty()) {
            return new ResponseEntity<>(compaignsWithPagination, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> retrieveById(@PathVariable Long id){
        Campaign campaign = campaignService.retrieveById(id);
        if(campaign != null)
            return new ResponseEntity<>(campaign, HttpStatus.OK);
        else
            return new ResponseEntity<>("campaign does not exist", HttpStatus.NOT_FOUND);
    }
    @GetMapping("/getStatById/{id}")
    public ResponseEntity<?> retrieveStatsById(@PathVariable Long id){
        CampaignStats campaign = campaignService.getCampaignStats(id);
        if(campaign != null)
            return new ResponseEntity<>(campaign, HttpStatus.OK);
        else
            return new ResponseEntity<>("campaign does not exist", HttpStatus.NOT_FOUND);
    }
    @GetMapping("/getStats/{clientId}/{filter}")
    public ResponseEntity<?> retrieveStats(@PathVariable Long clientId,
                                           @PathVariable String filter){
        List<CampaignStats> campaign = campaignService.getAllFilteredCampaignStats(clientId,filter);
        if(campaign != null)
            return new ResponseEntity<>(campaign, HttpStatus.OK);
        else
            return new ResponseEntity<>("campaign does not exist", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/countCompaign")
    public ResponseEntity<Long> getCompaignCount() {
        long count = campaignService.getCampaignCount();
        return ResponseEntity.ok(count);
    }

    @PutMapping("update/{id}/{name}")
    public ResponseEntity<String> updateCampaign(@RequestPart("campaign") Campaign campaign,
                                                 @PathVariable Long id,
                                                 @PathVariable("name") String name) {
        if (campaignService.updateCampaign(campaign,id,name)) {
            return new ResponseEntity<>("updated", HttpStatus.OK);

        } else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);

    }

    @PutMapping("disableAll/{id}")
    public ResponseEntity<String> disableAll(@PathVariable("id") Long idCompaign) {
        if (campaignService.disableAll(idCompaign)) {
            return new ResponseEntity<>("updated", HttpStatus.OK);

        } else return new ResponseEntity<>("error", HttpStatus.NOT_ACCEPTABLE);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id) {
        campaignService.deleteCampaign(id);
        return new ResponseEntity<>("deleted", HttpStatus.OK);
    }
}
