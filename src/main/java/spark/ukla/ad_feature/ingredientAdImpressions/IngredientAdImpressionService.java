package spark.ukla.ad_feature.ingredientAdImpressions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.ad_feature.FilterInsights;
import spark.ukla.ad_feature.campaign.Campaign;
import spark.ukla.ad_feature.campaign.CampaignRepository;
import spark.ukla.ad_feature.ingrediantAd.IngredientAd;
import spark.ukla.ad_feature.ingrediantAd.IngredientAdRepository;
import spark.ukla.entities.User;
import spark.ukla.entities.enums.Gender;
import spark.ukla.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Slf4j
@Service
@Transactional
public class IngredientAdImpressionService {
    private final IngredientAdImpressionRepository ingredientAdImpressionRepository;
    private final IngredientAdRepository ingredientAdRepository;
    private final CampaignRepository campaignRepository;

    private final UserRepository userRepository;

    public IngredientAdImpressionService(IngredientAdImpressionRepository ingredientAdImpressionRepository,CampaignRepository campaignRepository, UserRepository userRepository, IngredientAdRepository ingredientAdRepository){
        this.ingredientAdImpressionRepository = ingredientAdImpressionRepository;
        this.ingredientAdRepository=ingredientAdRepository;
        this.userRepository=userRepository;
        this.campaignRepository=campaignRepository;

    }

    public IngredientAdImpression add(User user, IngredientAd ingredientAd) {
        IngredientAdImpression ingredientAdImpressions=new IngredientAdImpression();

        ingredientAdImpressions.setViews(0);
        ingredientAdImpressions.setImpressions(0);
        ingredientAdImpressions.setIngredientAd(ingredientAd);
        ingredientAdImpressions.setUserId(user.getId());
        ingredientAdImpressionRepository.save(ingredientAdImpressions);

        return ingredientAdImpressions;
    }



    public boolean incrementViews (List<Long> ingredientAdsIDs,String username) {
        User user= userRepository.findByUsername(username);

        if (!ingredientAdsIDs.isEmpty()){
            // list ingredient ad impression of ids viewed
            List<Long> incrementedViewIds=new ArrayList<>();
            for (long id:ingredientAdsIDs
                 ) {
                IngredientAd ingredientAd=ingredientAdRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("ingredient ad not found ! "));

                boolean userExistence =ingredientAdImpressionRepository.existsByUserIdAndIngredientAd(user.getId(),ingredientAd);

                List<IngredientAdImpression> ingredientAdImpressions= ingredientAdImpressionRepository.findByIngredientAd(ingredientAd);
                if (!ingredientAdImpressions.isEmpty()){
                for (IngredientAdImpression ingredientAdImpression:ingredientAdImpressions
                     ) {
                    if (ingredientAdImpression==null || !userExistence){
                        IngredientAdImpression ingredientAdImpressionsAdded=add(user,ingredientAd);
                        incrementedViewIds.add(ingredientAdImpressionsAdded.getId());

                    }else incrementedViewIds.add(ingredientAdImpression.getId());

                }
                }else {
                        IngredientAdImpression ingredientAdImpressionsAdded=add(user,ingredientAd);
                        incrementedViewIds.add(ingredientAdImpressionsAdded.getId());
                }
            }
            ingredientAdImpressionRepository.incrementViews(incrementedViewIds);
            return true;
        }else return false;
    }

    public boolean incrementImpressions (List<Long> ingredientAdIds,String username) {
        User user= userRepository.findByUsername(username);

        if (!ingredientAdIds.isEmpty()){
            // list of ingredient ad impression ids
            List<Long> incrementedImpressionIds=new ArrayList<>();
            for (long id:ingredientAdIds
            ) {
                IngredientAd ingredientAd=ingredientAdRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("ingredient ad not found ! "));



                boolean userExistence=ingredientAdImpressionRepository.existsByUserIdAndIngredientAd(user.getId(),ingredientAd);

                List<IngredientAdImpression> ingredientAdImpressions= ingredientAdImpressionRepository.findByIngredientAd(ingredientAd);
                if (!ingredientAdImpressions.isEmpty()){
                    for (IngredientAdImpression ingredientAdImpression:ingredientAdImpressions
                    ) {
                        if (ingredientAdImpression==null || !userExistence){
                            IngredientAdImpression ingredientAdImpressionsAdded=add(user,ingredientAd);
                            incrementedImpressionIds.add(ingredientAdImpressionsAdded.getId());

                        } else incrementedImpressionIds.add(ingredientAdImpression.getId());

                    }
                }else {
                    IngredientAdImpression ingredientAdImpressionsAdded=add(user,ingredientAd);
                    incrementedImpressionIds.add(ingredientAdImpressionsAdded.getId());
                }

            }
            ingredientAdImpressionRepository.incrementImpressions(incrementedImpressionIds);
            return true;
        }else return false;
    }


    public List<IngredientAdStat> getIngredientAdStats (Long campaignId){
        Campaign campaign=campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("campaign not found ! "));
        List<IngredientAdImpression> ingredientAdImpressions=new ArrayList<>();
        for (IngredientAd ingredientAd:campaign.getIngredientAds()
        ) {
            List<IngredientAdImpression> ingredientAdImpressionList= ingredientAdImpressionRepository.findByIngredientAd(ingredientAd);
            ingredientAdImpressions.addAll(ingredientAdImpressionList);
        }

        return calculateIngredientAdStatsByIngredientAdImpressions(ingredientAdImpressions);
    }
    public List<IngredientAdStat> getFilteredIngredientAdStats(Long campaignId, String filter){
        Campaign campaign=campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("campaign not found ! "));
        List<IngredientAdImpression> ingredientAdImpressions=new ArrayList<>();
        for (IngredientAd ingredientAd:campaign.getIngredientAds()
        ) {
            List<IngredientAdImpression> ingredientAdImpressionList= ingredientAdImpressionRepository.findByIngredientAd(ingredientAd);
            ingredientAdImpressions.addAll(ingredientAdImpressionList);
        }

        return calculateIngredientAdStatsByUserAge(ingredientAdImpressions,filter);
    }

    public List<IngredientAdStat> getFilteredIngredientAdStatsByGender(Long campaignId, String filter){
        Campaign campaign=campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("campaign not found ! "));
        List<IngredientAdImpression> ingredientAdImpressions=new ArrayList<>();
        for (IngredientAd ingredientAd:campaign.getIngredientAds()
        ) {
            List<IngredientAdImpression> ingredientAdImpressionList= ingredientAdImpressionRepository.findByIngredientAd(ingredientAd);
            ingredientAdImpressions.addAll(ingredientAdImpressionList);
        }

        return calculateIngredientAdStatsByGender(ingredientAdImpressions,filter);
    }

    List<IngredientAdStat> calculateIngredientAdStatsByIngredientAdImpressions(List<IngredientAdImpression> ingredientAdImpressions){
        List<IngredientAdStat> ingredientAdStats=new ArrayList<>();
        List<Long> ingredientAdStatIDs=new ArrayList<>();

        for (IngredientAdImpression i:ingredientAdImpressions
             ) {
            IngredientAdStat ingredientAdStat=new IngredientAdStat();
            if (ingredientAdStats.isEmpty() || !ingredientAdStatIDs.contains(i.getIngredientAd().getId())){
                ingredientAdStat.setIngredientAdId(i.getIngredientAd().getId());
                ingredientAdStat.setImpressions(i.getImpressions());
                ingredientAdStat.setViews(i.getViews());
                ingredientAdStat.setReach(i.getIngredientAd().getReach());
                ingredientAdStat.setStatus(i.getIngredientAd().isActive());
                ingredientAdStat.setFrequency(calculateFrequency(i.getImpressions(),i.getIngredientAd().getReach()));
                ingredientAdStat.setAmount_spent(calculateAmount(i.getViews()));
                ingredientAdStat.setView_rate(calculateViewRate(i.getImpressions(),i.getViews()));
                ingredientAdStat.setName(i.getIngredientAd().getBrandName());
                ingredientAdStat.setType("Ingredient Ad");
                ingredientAdStats.add(ingredientAdStat);
            }else {
                for (IngredientAdStat is:ingredientAdStats
                     ) {
                    int isImpressions= is.getImpressions();
                    int isViews= is.getViews();
                    int isReach= i.getIngredientAd().getReach();

                    isImpressions+=i.getImpressions();
                    isViews+=i.getViews();

                    if (is.getIngredientAdId().equals(i.getIngredientAd().getId()) ){
                        is.setImpressions(isImpressions);
                        is.setViews(isViews);
                        is.setReach(isReach);
                        is.setStatus(i.getIngredientAd().isActive());
                        is.setAmount_spent(calculateAmount(isViews));
                        is.setFrequency(calculateFrequency(isImpressions,isReach));
                        is.setView_rate(calculateViewRate(isImpressions,isViews));
                    }


                }

            }
            ingredientAdStatIDs.add(i.getIngredientAd().getId());
        }
        return ingredientAdStats;
    }
    List<IngredientAdStat> calculateIngredientAdStatsByUserAge(List<IngredientAdImpression> ingredientAdImpressions,String filter){
        List<IngredientAdStat> ingredientAdStats=new ArrayList<>();
        List<Long> ingredientAdStatIDs=new ArrayList<>();

        for (IngredientAdImpression i:ingredientAdImpressions
        ) {
            IngredientAdStat ingredientAdStat=new IngredientAdStat();
            Long userId=i.getUserId();
            User user=userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("user not found ! "));
            FilterInsights filterInsights= classifyUsersByAge(user);
            if (filterInsights==FilterInsights.valueOf(filter)) {
                if (ingredientAdStats.isEmpty() || !ingredientAdStatIDs.contains(i.getIngredientAd().getId())) {
                    ingredientAdStat.setIngredientAdId(i.getIngredientAd().getId());
                    ingredientAdStat.setImpressions(i.getImpressions());
                    ingredientAdStat.setViews(i.getViews());
                    ingredientAdStat.setStatus(i.getIngredientAd().isActive());
                    ingredientAdStat.setReach(i.getIngredientAd().getReach());
                    ingredientAdStat.setFrequency(calculateFrequency(i.getImpressions(),i.getIngredientAd().getReach()));
                    ingredientAdStat.setView_rate(calculateViewRate(i.getImpressions(),i.getViews()));
                    ingredientAdStat.setAmount_spent(calculateAmount(i.getViews()));
                    ingredientAdStat.setName(i.getIngredientAd().getBrandName());
                    ingredientAdStat.setType("Ingredient Ad");
                    ingredientAdStats.add(ingredientAdStat);
                } else {
                    for (IngredientAdStat is : ingredientAdStats
                    ) {
                        int isImpressions = is.getImpressions();
                        int isViews = is.getViews();
                        int isReach = i.getIngredientAd().getReach();

                        isImpressions += i.getImpressions();
                        isViews += i.getViews();

                        if (is.getIngredientAdId().equals(i.getIngredientAd().getId())  ) {
                            is.setImpressions(isImpressions);
                            is.setViews(isViews);
                            is.setReach(isReach);
                            is.setStatus(i.getIngredientAd().isActive());

                            is.setFrequency(calculateFrequency(isImpressions,isReach));
                            is.setView_rate(calculateViewRate(isImpressions,isViews));
                            is.setAmount_spent(calculateAmount(isViews));

                        }


                    }

                }
                ingredientAdStatIDs.add(i.getIngredientAd().getId());
            }else {
                if (ingredientAdStats.isEmpty() || !ingredientAdStatIDs.contains(i.getIngredientAd().getId())) {
                    ingredientAdStat.setIngredientAdId(i.getIngredientAd().getId());
                    ingredientAdStat.setImpressions(0);
                    ingredientAdStat.setViews(0);
                    ingredientAdStat.setStatus(i.getIngredientAd().isActive());
                    ingredientAdStat.setName(i.getIngredientAd().getBrandName());
                    ingredientAdStat.setType("Ingredient Ad");
                    ingredientAdStats.add(ingredientAdStat);
                } else {
                    for (IngredientAdStat is : ingredientAdStats
                    ) {
                        int isImpressions = is.getImpressions();
                        int isViews = is.getViews();
                        int isReach = i.getIngredientAd().getReach();



                        if (is.getIngredientAdId().equals(i.getIngredientAd().getId())) {
                            is.setImpressions(isImpressions);
                            is.setViews(isViews);
                            is.setReach(isReach);
                            is.setStatus(i.getIngredientAd().isActive());

                            is.setAmount_spent(calculateAmount(isViews));
                            is.setFrequency(calculateFrequency(isImpressions,isReach));
                            is.setView_rate(calculateViewRate(isImpressions,isViews));
                        }


                    }

                }
                ingredientAdStatIDs.add(i.getIngredientAd().getId());
            }
        }
        return ingredientAdStats;
    }
    List<IngredientAdStat> calculateIngredientAdStatsByGender(List<IngredientAdImpression> ingredientAdImpressions,String filter){
        List<IngredientAdStat> ingredientAdStats=new ArrayList<>();
        List<Long> ingredientAdStatIDs=new ArrayList<>();

        for (IngredientAdImpression i:ingredientAdImpressions
        ) {
            IngredientAdStat ingredientAdStat=new IngredientAdStat();
            Long userId=i.getUserId();
            User user=userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("user not found ! "));
            FilterInsights filterInsights= classifyUsersByGender(user);
            if (filterInsights==FilterInsights.valueOf(filter)) {
                if (ingredientAdStats.isEmpty() || !ingredientAdStatIDs.contains(i.getIngredientAd().getId())) {
                    ingredientAdStat.setIngredientAdId(i.getIngredientAd().getId());
                    ingredientAdStat.setImpressions(i.getImpressions());
                    ingredientAdStat.setViews(i.getViews());
                    ingredientAdStat.setStatus(i.getIngredientAd().isActive());
                    ingredientAdStat.setReach(i.getIngredientAd().getReach());
                    ingredientAdStat.setFrequency(calculateFrequency(i.getImpressions(),i.getIngredientAd().getReach()));
                    ingredientAdStat.setView_rate(calculateViewRate(i.getImpressions(),i.getViews()));
                    ingredientAdStat.setAmount_spent(calculateAmount(i.getViews()));
                    ingredientAdStat.setName(i.getIngredientAd().getBrandName());
                    ingredientAdStat.setType("Ingredient Ad");
                    ingredientAdStats.add(ingredientAdStat);
                } else {
                    for (IngredientAdStat is : ingredientAdStats
                    ) {
                        int isImpressions = is.getImpressions();
                        int isViews = is.getViews();
                        int isReach = i.getIngredientAd().getReach();

                        isImpressions += i.getImpressions();
                        isViews += i.getViews();

                        if (is.getIngredientAdId().equals(i.getIngredientAd().getId()) ) {
                            is.setImpressions(isImpressions);
                            is.setViews(isViews);
                            is.setReach(isReach);
                            is.setStatus(i.getIngredientAd().isActive());

                            is.setFrequency(calculateFrequency(isImpressions,isReach));
                            is.setView_rate(calculateViewRate(isImpressions,isViews));
                            is.setAmount_spent(calculateAmount(isViews));

                        }


                    }

                }
                ingredientAdStatIDs.add(i.getIngredientAd().getId());
            }else {
                if (ingredientAdStats.isEmpty() || !ingredientAdStatIDs.contains(i.getIngredientAd().getId())) {
                    ingredientAdStat.setIngredientAdId(i.getIngredientAd().getId());
                    ingredientAdStat.setImpressions(0);
                    ingredientAdStat.setViews(0);
                    ingredientAdStat.setReach(0);
                    ingredientAdStat.setStatus(i.getIngredientAd().isActive());
                    ingredientAdStat.setName(i.getIngredientAd().getBrandName());
                    ingredientAdStat.setType("Ingredient Ad");
                    ingredientAdStats.add(ingredientAdStat);
                } else {
                    for (IngredientAdStat is : ingredientAdStats
                    ) {
                        int isImpressions = is.getImpressions();
                        int isViews = is.getViews();
                        int isReach = i.getIngredientAd().getReach();



                        if (is.getIngredientAdId().equals(i.getIngredientAd().getId())  ) {
                            is.setImpressions(isImpressions);
                            is.setViews(isViews);
                            is.setReach(isReach);
                            is.setStatus(i.getIngredientAd().isActive());

                            is.setAmount_spent(calculateAmount(isViews));
                            is.setFrequency(calculateFrequency(isImpressions,isReach));
                            is.setView_rate(calculateViewRate(isImpressions,isViews));
                        }


                    }

                }
                ingredientAdStatIDs.add(i.getIngredientAd().getId());
            }
        }
        return ingredientAdStats;
    }
    public FilterInsights classifyUsersByAge(User user) {
        Date birthdate = user.getBirthdate();
        int age = calculateAge(birthdate);
        if (age >= 18 && age <= 24) {
            return FilterInsights.Age_18_24;
        } else if (age >= 25 && age <= 34) {
            return FilterInsights.Age_25_34;
        } else if (age >= 35 && age <= 44) {
            return FilterInsights.Age_35_44;
        } else if (age >= 45 && age <= 54) {
            return FilterInsights.Age_45_54;
        } else if (age >= 55 && age <= 64) {
            return FilterInsights.Age_55_64;
        } else if (age >= 65) {
            return FilterInsights.Age_plus_65;
        } else {
            throw new IllegalArgumentException("Age not in valid range");
        }
    }

    public FilterInsights classifyUsersByGender(User user) {
        Gender gender = user.getGender();
        if (gender.equals(Gender.Male)) {
            return FilterInsights.Male;
        } else if (gender.equals(Gender.Female)) {
            return FilterInsights.Female;
        } else if (gender.equals(Gender.Other)) {
            return FilterInsights.Other;
        } else {
            throw new IllegalArgumentException("Gender not valid ");
        }
    }

    private int calculateAge(Date birthdate) {
        Calendar birthCalendar = Calendar.getInstance();
        birthCalendar.setTime(birthdate);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    public float calculateFrequency(int impression, int reach) {
        if (reach != 0) {
            return Math.round(((float) impression / reach) * 100) / 100.0f;
        }
        return 0;
    }

    public float calculateViewRate(int impression, int view) {
        if (impression != 0) {
            return Math.round(((float) view / impression) * 10000) / 100.0f;
        }
        return 0;
    }

    public float calculateAmount(int views) {
        return Math.round((0.05F * views) * 100) / 100.0f;
    }
}
