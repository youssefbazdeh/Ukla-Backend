package spark.ukla.ad_feature.ingrediantAd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.ad_feature.campaign.Campaign;
import spark.ukla.ad_feature.campaign.CampaignRepository;
import spark.ukla.entities.Image;
import spark.ukla.entities.Ingredient;
import spark.ukla.repositories.ImageDbRepository;
import spark.ukla.repositories.IngredientRepository;
import spark.ukla.services.implementations.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
public class IngredientAdService {
    private final IngredientAdRepository ingredientAdRepository;
    private final IngredientRepository ingredientRepository;
    private final CampaignRepository campaignRepository;
    private final FileLocationService fileLocationService;
    private final ImageDbRepository imageDbRepository;
    public IngredientAdService(IngredientAdRepository ingredientAdRepository,IngredientRepository ingredientRepository,CampaignRepository campaignRepository,FileLocationService fileLocationService,ImageDbRepository imageDbRepository) {
        this.fileLocationService = fileLocationService;
        this.imageDbRepository = imageDbRepository;
        this.ingredientAdRepository = ingredientAdRepository;
        this.ingredientRepository = ingredientRepository;
        this.campaignRepository = campaignRepository;
    }

    public IngredientAd addIngredientAdAndSetIngredientId(IngredientAd ingredientAd,String ingrediantname, Image image,Long campaignId) {
        Ingredient ingredient=ingredientRepository.findByName(ingrediantname);
        if(ingredientAdRepository.findByIngredientIdAndCountryCode(ingredient.getId(),ingredientAd.getCountryCode())!=null){
            return null;
        }
        ingredientAd.setViews(0);
        ingredientAd.setImage(image);
        ingredientAd.setIngredientId(ingredient.getId());
        ingredientAdRepository.save(ingredientAd);
        Campaign campaign=campaignRepository.findById(campaignId).get();
        List<IngredientAd> ingredientAdList=campaign.getIngredientAds();
        ingredientAdList.add(ingredientAd);
        campaign.setIngredientAds(ingredientAdList);
        return ingredientAd;
    }

    public List<IngredientAd> getIngredientAdsWithPagination(int page, int size,Long id){
        Pageable pageable;
        Campaign campaign=campaignRepository.findById(id).get();
        if (page == 0 && size == 0) {

            return campaign.getIngredientAds();

        }

        pageable = PageRequest.of(page - 1, size);
        List<Page<IngredientAd>> ingredientAdList=new ArrayList<>();
        for (IngredientAd i:campaign.getIngredientAds()
             ) {

            Page<IngredientAd> ingredientAdPage = ingredientAdRepository.findAllById(pageable,i.getId());
            ingredientAdList.add(ingredientAdPage);
        }

        List<IngredientAd> combinedIngredientAds = new ArrayList<>();
        for (Page<IngredientAd> p : ingredientAdList) {
            combinedIngredientAds.addAll(p.getContent());
        }
        Page<IngredientAd> p =new PageImpl<>(combinedIngredientAds, pageable, combinedIngredientAds.size());
        return p.getContent();
    }
    public long getCount() {
        return ingredientAdRepository.count();
    }
    public IngredientAd retrieveById(Long id) {
        IngredientAd ingredientAd = ingredientAdRepository.findById(id).get();
        if (ingredientAd!=null){
            return ingredientAd;
        }else
            return null;
    }

    public void deleteByid(Long idIngredientAd) {
        IngredientAd ingredientAd=ingredientAdRepository.findById(idIngredientAd).get();
        campaignRepository.removecampaign_ingredient_adsAssociation(idIngredientAd);
        fileLocationService.deleteImage(ingredientAd.getImage().getLocation());
        imageDbRepository.delete(ingredientAd.getImage());
        ingredientAdRepository.deleteById(idIngredientAd);
    }

    public  boolean activate(Long ingredientAdID){
        IngredientAd ingredientAd=ingredientAdRepository.findById(ingredientAdID).get();
        if (ingredientAd!=null){
            ingredientAd.setActive(true);
            return true;
        }else return false;
    }

    public boolean disable(Long ingrediantID){
        IngredientAd ingredientAd=ingredientAdRepository.findById(ingrediantID).get();
        if (!ingredientAd.isActive()){
            return false;
        }
        ingredientAd.setActive(true);
        return true;
    }
    public String updateImage(Image image, Long id) {
        IngredientAd ingredientAd = ingredientAdRepository.findById(id).orElse(null);
        if(ingredientAd == null)
            return "ingredient ad not found";
        fileLocationService.deleteImage(ingredientAd.getImage().getLocation());
        imageDbRepository.delete(ingredientAd.getImage());
        ingredientAd.setImage(image);
        ingredientAdRepository.save(ingredientAd);
        return "image updated";
    }
    public boolean updateAd(IngredientAd ingredientAd, long id,String ingredientName) {
        Ingredient ingredient=ingredientRepository.findByName(ingredientName);
        IngredientAd existingAd = ingredientAdRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ingredientAd not found ! "));
        if (!(existingAd.getCountryCode()==ingredientAd.getCountryCode() && existingAd.getIngredientId()==ingredient.getId())){
            if(ingredientAdRepository.findByIngredientIdAndCountryCode(ingredient.getId(),ingredientAd.getCountryCode())!=null){
                return false;
            }
        }


        existingAd.setIngredientId(ingredient.getId());
        existingAd.setActive(ingredientAd.isActive());
        existingAd.setBrandName(ingredientAd.getBrandName());
        existingAd.setCountryCode(ingredientAd.getCountryCode());
        ingredientAdRepository.save(existingAd);
        return true;
    }

    public List<IngredientAd> getAllByIngredientIdsAndActiveAndCountryCode(List<Long> idsList, CountryCode countryCode){
        List<IngredientAd> ingredientAds = ingredientAdRepository.getAllByIngredientIdAndActiveAndCountryCode(idsList,true,countryCode);
        if(ingredientAds.isEmpty()){
            return Collections.emptyList();
        }
        return ingredientAds;
    }

}
