package spark.ukla.ad_feature.campaign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.ad_feature.FilterInsights;
import spark.ukla.ad_feature.bannerAd.BannerAd;
import spark.ukla.ad_feature.bannerAdImpressions.BannerAdImpressionService;
import spark.ukla.ad_feature.client.Client;
import spark.ukla.ad_feature.client.ClientRepository;
import spark.ukla.ad_feature.ingrediantAd.IngredientAd;
import spark.ukla.ad_feature.ingredientAdImpressions.IngredientAdImpressionService;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final ClientRepository clientRepository;
    private final BannerAdImpressionService bannerAdImpressionService;
    private final IngredientAdImpressionService ingredientAdImpressionService;
    public CampaignService(CampaignRepository campaignRepository,ClientRepository clientRepository,BannerAdImpressionService bannerAdImpressionService,IngredientAdImpressionService ingredientAdImpressionService){
        this.campaignRepository=campaignRepository;
        this.bannerAdImpressionService=bannerAdImpressionService;
        this.ingredientAdImpressionService=ingredientAdImpressionService;
        this.clientRepository=clientRepository;

    }

    public boolean addCampaign(Campaign campaign,String companyName) {
        Client client=clientRepository.findByCompanyName(companyName);
        if (client!=null){
            campaign.setClient(client);
            campaignRepository.save(campaign);
            return true;
        }
        return false;
    }
    public List<Campaign> getCampaignsWithPagination(int page, int size){
        Pageable pageable;
        if (page == 0 && size == 0) {
            return campaignRepository.findAll();

        }

        pageable = PageRequest.of(page - 1, size);

        Page<Campaign> firstPage = campaignRepository.findAll(pageable);
        return  firstPage.getContent();
    }

    public CampaignStats getCampaignStats(Long idCampaign){
        Optional<Campaign> optionalCampaign = campaignRepository.findById(idCampaign);

        if (!optionalCampaign.isPresent()) {
            throw new EntityNotFoundException("Campaign with ID " + idCampaign + " not found");
        }

        Campaign campaign = optionalCampaign.get();

        CampaignStats campaignStats = new CampaignStats();
        campaignStats.setBannerAdStats(bannerAdImpressionService.getBannerAdStats(idCampaign));
        campaignStats.setIngredientAdStats(ingredientAdImpressionService.getIngredientAdStats(idCampaign));
        campaignStats.setBudget(campaign.getBudget());
        campaignStats.setCampaignName(campaign.getCampaignName());
        return campaignStats;
    }
    public List<CampaignStats> getAllFilteredCampaignStats(Long idClient, String filter){
        List<Campaign> campaignList= campaignRepository.findAllByClientId(idClient);

        List<CampaignStats> campaignStatsList = new ArrayList<>();

        for (Campaign c:campaignList
             ) {
            CampaignStats campaignStats = new CampaignStats();
            if (FilterInsights.valueOf(filter)==FilterInsights.Banners ){
                if (!c.getBannerAds().isEmpty()){
                    campaignStats.setBannerAdStats(bannerAdImpressionService.getBannerAdStats(c.getId()));
                    campaignStats.setIngredientAdStats(null);

                    campaignStats.setBudget(c.getBudget());
                    campaignStats.setCampaignName(c.getCampaignName());
                    campaignStatsList.add(campaignStats);
                }

            } else if (FilterInsights.valueOf(filter)==FilterInsights.Ingredients ) {
                if ( !c.getIngredientAds().isEmpty()){
                    campaignStats.setIngredientAdStats(ingredientAdImpressionService.getIngredientAdStats(c.getId()));
                    campaignStats.setBannerAdStats(new ArrayList<>());

                    campaignStats.setBudget(c.getBudget());
                    campaignStats.setCampaignName(c.getCampaignName());
                    campaignStatsList.add(campaignStats);
                }

            } else if (FilterInsights.valueOf(filter)==FilterInsights.All) {
                campaignStats.setBannerAdStats(bannerAdImpressionService.getBannerAdStats(c.getId()));
                campaignStats.setIngredientAdStats(ingredientAdImpressionService.getIngredientAdStats(c.getId()));
                campaignStats.setBudget(c.getBudget());
                campaignStats.setCampaignName(c.getCampaignName());
                campaignStatsList.add(campaignStats);
            }else if (FilterInsights.valueOf(filter)==FilterInsights.Male || FilterInsights.valueOf(filter)==FilterInsights.Female ||FilterInsights.valueOf(filter)==FilterInsights.Other) {
                campaignStats.setBannerAdStats(bannerAdImpressionService.getFilteredBannerAdStatsByUserGender(c.getId(),filter));
                campaignStats.setIngredientAdStats(ingredientAdImpressionService.getFilteredIngredientAdStatsByGender(c.getId(),filter));
                campaignStats.setBudget(c.getBudget());
                campaignStats.setCampaignName(c.getCampaignName());
                campaignStatsList.add(campaignStats);
            } else{
                campaignStats.setBannerAdStats(bannerAdImpressionService.getFilteredBannerAdStats(c.getId(),filter));
                campaignStats.setIngredientAdStats(ingredientAdImpressionService.getFilteredIngredientAdStats(c.getId(),filter));
                campaignStats.setBudget(c.getBudget());
                campaignStats.setCampaignName(c.getCampaignName());
                campaignStatsList.add(campaignStats);
            }


        }

        return campaignStatsList;
    }



    public long getCampaignCount() {
        return campaignRepository.count();
    }
    public Campaign retrieveById(Long idCampaign) {
        Optional<Campaign> optionalCampaign = campaignRepository.findById(idCampaign);

        if (!optionalCampaign.isPresent()) {
            throw new EntityNotFoundException("Campaign with ID " + idCampaign + " not found");
        }

        return optionalCampaign.get();

    }
    public boolean updateCampaign(Campaign ce, long id,String companyName) {
        Client client=clientRepository.findByCompanyName(companyName);
        Campaign existingCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Campaign not found ! "));

        existingCampaign.setClient(client);
        existingCampaign.setCampaignName(ce.getCampaignName());
        existingCampaign.setActive(ce.isActive());
        existingCampaign.setCountryCode(ce.getCountryCode());
        existingCampaign.setViewsObjective(ce.getViewsObjective());
        existingCampaign.setBudget(ce.getBudget());
        campaignRepository.save(existingCampaign);
        return true;
    }
    public void deleteCampaign(Long idCampaign) {
        campaignRepository.deleteById(idCampaign);
    }
    public boolean disableAll(Long idCampaign){
        Campaign campaign= campaignRepository.findById(idCampaign).orElse(null);
        if (campaign!=null){
            if (campaign.getBannerAds()!=null){
                List<BannerAd> bannerAds= campaign.getBannerAds();
                for (BannerAd ba:bannerAds
                ) {
                    ba.setActive(false);
                }
            }
            if (campaign.getBannerAds()!=null){
                List<IngredientAd> ingredientAds=campaign.getIngredientAds();
                for (IngredientAd ia:ingredientAds
                ) {
                    ia.setActive(false);
                }
            }
            campaign.setActive(false);
            return true;
        }else
            return false;
    }
}
