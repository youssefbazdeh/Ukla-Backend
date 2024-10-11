package spark.ukla.ad_feature.bannerAdImpressions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.ad_feature.FilterInsights;
import spark.ukla.ad_feature.bannerAd.BannerAd;
import spark.ukla.ad_feature.bannerAd.BannerAdRepository;
import spark.ukla.ad_feature.campaign.Campaign;
import spark.ukla.ad_feature.campaign.CampaignRepository;
import spark.ukla.entities.User;
import spark.ukla.entities.enums.Gender;
import spark.ukla.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Slf4j
@Service
@Transactional
public class BannerAdImpressionService {
    private final BannerAdImpressionRepository bannerAdImpressionRepository;
    private final BannerAdRepository bannerAdRepository;
    private final UserRepository userRepository;

    private final CampaignRepository campaignRepository;

    public BannerAdImpressionService(BannerAdImpressionRepository bannerAdImpressionRepository,CampaignRepository campaignRepository, UserRepository userRepository, BannerAdRepository bannerAdRepository){
        this.bannerAdImpressionRepository = bannerAdImpressionRepository;
        this.userRepository=userRepository;
        this.bannerAdRepository=bannerAdRepository;
        this.campaignRepository=campaignRepository;

    }

    public BannerAdImpression add( User user, BannerAd bannerAd) {
        BannerAdImpression bannerAdImpression = new BannerAdImpression();

        bannerAdImpression.setViews(0);
        bannerAdImpression.setClicks(0);
        bannerAdImpression.setClicksOnSkip(0);
        bannerAdImpression.setPt25(0);
        bannerAdImpression.setPt50(0);
        bannerAdImpression.setPt75(0);
        bannerAdImpression.setPt100(0);
        bannerAdImpression.setUserId(user.getId());
        bannerAdImpression.setBannerAd(bannerAd);
        bannerAdImpressionRepository.save(bannerAdImpression);

        return bannerAdImpression;
    }

    public boolean incrementBannerAdImpressionMetrics(Long bannerAdID, String username, BannerMetricsIncrementor bannerMetricsIncrementor) {
        User user= userRepository.findByUsername(username);

        BannerAd bannerAd = bannerAdRepository.findById(bannerAdID).orElse(null);
        if (bannerAd != null && bannerMetricsIncrementor != null) {

            boolean userExistence= bannerAdImpressionRepository.existsByUserIdAndAndBannerAd(user.getId(),bannerAd);

            if (userExistence){
                BannerAdImpression bannerAdImpression=bannerAdImpressionRepository.findBannerAdImpressionByBannerAdAndAndUserId(bannerAd,user.getId());

                incrementMetrics(bannerMetricsIncrementor,bannerAdImpression);
            }else {
                BannerAdImpression bannerAdImpressionAdded = add(user, bannerAd);
                //todo make the new entity be saved after incrementing it's values so we don't save the same entity twice in a row
                incrementMetrics(bannerMetricsIncrementor,bannerAdImpressionAdded);

            }

            return true;
        } else {
            return false;
        }
    }

    public void incrementMetrics(BannerMetricsIncrementor bannerMetricsIncrementor,BannerAdImpression bannerAdImpression){
        if (bannerMetricsIncrementor.isViews()){
            bannerAdImpression.incrementViews();
        }
        if (bannerMetricsIncrementor.isClick()) {
            bannerAdImpression.incrementClicks();
        }
        if (bannerMetricsIncrementor.isClicksOnSkip()) {
            bannerAdImpression.incrementclicksOnSkip();
        }
        if (bannerMetricsIncrementor.isPt25()) {
            bannerAdImpression.incrementPt25();
        }
        if (bannerMetricsIncrementor.isPt50()) {
            bannerAdImpression.incrementPt50();
        }
        if (bannerMetricsIncrementor.isPt75()) {
            bannerAdImpression.incrementPt75();
        }
        if (bannerMetricsIncrementor.isPt100()) {
            bannerAdImpression.incrementPt100();
        }
        bannerAdImpressionRepository.save(bannerAdImpression);

    }
    public List<BannerAdStat> getBannerAdStats (Long campaignId){
        Campaign campaign=campaignRepository.findById(campaignId).orElseThrow(() -> new EntityNotFoundException("Campaign with ID " + campaignId + " not found"));
        List<BannerAdImpression> bannerAdImpressions=new ArrayList<>();
        for (BannerAd bannerAd:campaign.getBannerAds()
             ) {
            List<BannerAdImpression> bannerAdImpressionList=bannerAdImpressionRepository.findByBannerAd(bannerAd);
            bannerAdImpressions.addAll(bannerAdImpressionList);

        }
        return calculateBannerAdStatsByIngredientAdImpressions(bannerAdImpressions);
    }
    public List<BannerAdStat> getFilteredBannerAdStats(Long campaignId, String filter){
        Campaign campaign=campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("campaign not found ! "));
        List<BannerAdImpression> bannerAdImpressions=new ArrayList<>();
        for (BannerAd bannerAd:campaign.getBannerAds()
        ) {
            List<BannerAdImpression> bannerAdImpressionList=bannerAdImpressionRepository.findByBannerAd(bannerAd);
            bannerAdImpressions.addAll(bannerAdImpressionList);

        }
        return calculateBannerAdStatsByUserAge(bannerAdImpressions,filter);
    }
    public List<BannerAdStat> getFilteredBannerAdStatsByUserGender(Long campaignId, String filter){
        Campaign campaign=campaignRepository.findById(campaignId)
                .orElseThrow(() -> new EntityNotFoundException("campaign not found ! "));
        List<BannerAdImpression> bannerAdImpressions=new ArrayList<>();
        for (BannerAd bannerAd:campaign.getBannerAds()
        ) {
            List<BannerAdImpression> bannerAdImpressionList=bannerAdImpressionRepository.findByBannerAd(bannerAd);
            bannerAdImpressions.addAll(bannerAdImpressionList);

        }
        return calculateBannerAdStatsByUserGender(bannerAdImpressions,filter);
    }

    List<BannerAdStat> calculateBannerAdStatsByIngredientAdImpressions(List<BannerAdImpression> bannerAdImpressions){
        List<BannerAdStat> bannerAdStats=new ArrayList<>();
        List<Long> bannerAdStatIDs=new ArrayList<>();

        for (BannerAdImpression b:bannerAdImpressions
        ) {
            BannerAdStat bannerAdStat=new BannerAdStat();

            if (bannerAdStats.isEmpty() || !bannerAdStatIDs.contains(b.getBannerAd().getId())){
                bannerAdStat.setBannerAdId(b.getBannerAd().getId());
                bannerAdStat.setClicks(b.getClicks());
                bannerAdStat.setReach(b.getBannerAd().getReach());
                bannerAdStat.setViews(b.getViews());
                bannerAdStat.setStatus(b.getBannerAd().isActive());

                bannerAdStat.setClicksOnSkip(b.getClicksOnSkip());
                bannerAdStat.setPt25(b.getPt25());
                bannerAdStat.setPt50(b.getPt50());
                bannerAdStat.setPt75(b.getPt75());
                bannerAdStat.setPt100(b.getPt100());
                bannerAdStat.setAmount_spent(calculateAmount(b.getViews()));
                bannerAdStat.setCpc(calculateCpc(bannerAdStat.getAmount_spent(), b.getClicks()));
                bannerAdStat.setCtr(calculateCtr(b.getViews(),b.getClicks()));
                bannerAdStat.setAvg_played_time(calculateAveragePercentage(b.getPt25(),b.getPt50(),b.getPt75(),b.getPt100()));
                bannerAdStat.setBannerName(b.getBannerAd().getBannerName());
                bannerAdStat.setType("Banner Ad");
                bannerAdStats.add(bannerAdStat);
            }else {
                for (BannerAdStat bs:bannerAdStats
                ) {
                    int bsClicks= bs.getClicks();
                    int bsViews= bs.getViews();
                    int bsReach= b.getBannerAd().getReach();
                    int bsClicksOnSkip= bs.getClicksOnSkip();
                    int bsPt25= bs.getPt25();
                    int bsPt50= bs.getPt50();
                    int bsPt75= bs.getPt75();
                    int bsPt100= bs.getPt100();


                    bsClicks+=b.getClicks();
                    bsViews+=b.getViews();
                    bsClicksOnSkip+=b.getClicksOnSkip();
                    bsPt25+=b.getPt25();
                    bsPt50+=b.getPt50();
                    bsPt75+=b.getPt75();
                    bsPt100+=b.getPt100();

                    if (bs.getBannerAdId().equals(b.getBannerAd().getId()) ){
                        bs.setClicks(bsClicks);
                        bs.setViews(bsViews);
                        bs.setReach(bsReach);
                        bs.setClicksOnSkip(bsClicksOnSkip);
                        bs.setPt25(bsPt25);
                        bs.setPt50(bsPt50);
                        bs.setPt75(bsPt75);
                        bs.setPt100(bsPt100);
                        bs.setStatus(b.getBannerAd().isActive());
                        bs.setAmount_spent(calculateAmount(bsViews));
                        bs.setCpc(calculateCpc(bs.getAmount_spent(), bsClicks));
                        bs.setCtr(calculateCtr(bsViews,bsClicks));
                        bs.setAvg_played_time(calculateAveragePercentage(bsPt25,bsPt50,bsPt75,bsPt100));
                    }


                }

            }
            bannerAdStatIDs.add(b.getBannerAd().getId());
        }
        return bannerAdStats;
    }

    List<BannerAdStat> calculateBannerAdStatsByUserAge(List<BannerAdImpression> bannerAdImpressions,String filter){
        List<BannerAdStat> bannerAdStats=new ArrayList<>();
        List<Long> bannerAdStatIDs=new ArrayList<>();

        for (BannerAdImpression b:bannerAdImpressions
        ) {
            BannerAdStat bannerAdStat=new BannerAdStat();
            Long userId=b.getUserId();
            User user=userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("user not found ! "));
            FilterInsights filterInsights= classifyUsersByAge(user);
            if (filterInsights==FilterInsights.valueOf(filter)){
                if (bannerAdStats.isEmpty() || !bannerAdStatIDs.contains(b.getBannerAd().getId())){
                    bannerAdStat.setBannerAdId(b.getBannerAd().getId());
                    bannerAdStat.setClicks(b.getClicks());
                    bannerAdStat.setViews(b.getViews());
                    bannerAdStat.setReach(b.getBannerAd().getReach());
                    bannerAdStat.setClicksOnSkip(b.getClicksOnSkip());
                    bannerAdStat.setPt25(b.getPt25());
                    bannerAdStat.setPt50(b.getPt50());
                    bannerAdStat.setPt75(b.getPt75());
                    bannerAdStat.setPt100(b.getPt100());
                    bannerAdStat.setAmount_spent(calculateAmount(b.getViews()));
                    bannerAdStat.setCpc(calculateCpc(bannerAdStat.getAmount_spent(), b.getClicks()));
                    bannerAdStat.setCtr(calculateCtr(b.getViews(),b.getClicks()));
                    bannerAdStat.setAvg_played_time(calculateAveragePercentage(b.getPt25(),b.getPt50(),b.getPt75(),b.getPt100()));
                    bannerAdStat.setBannerName(b.getBannerAd().getBannerName());
                    bannerAdStat.setStatus(b.getBannerAd().isActive());
                    bannerAdStat.setType("Banner Ad");
                    bannerAdStats.add(bannerAdStat);
                }else {
                    for (BannerAdStat bs:bannerAdStats
                    ) {
                        int bsClicks= bs.getClicks();
                        int bsViews= bs.getViews();
                        int bsReach= b.getBannerAd().getReach();
                        int bsClicksOnSkip= bs.getClicksOnSkip();
                        int bsPt25= bs.getPt25();
                        int bsPt50= bs.getPt50();
                        int bsPt75= bs.getPt75();
                        int bsPt100= bs.getPt100();


                        bsClicks+=b.getClicks();
                        bsViews+=b.getViews();
                        bsClicksOnSkip+=b.getClicksOnSkip();
                        bsPt25+=b.getPt25();
                        bsPt50+=b.getPt50();
                        bsPt75+=b.getPt75();
                        bsPt100+=b.getPt100();

                        if (bs.getBannerAdId().equals(b.getBannerAd().getId()) ){
                            bs.setClicks(bsClicks);
                            bs.setViews(bsViews);
                            bs.setReach(bsReach);
                            bs.setClicksOnSkip(bsClicksOnSkip);
                            bs.setPt25(bsPt25);
                            bs.setPt50(bsPt50);
                            bs.setPt75(bsPt75);
                            bs.setPt100(bsPt100);
                            bs.setAmount_spent(calculateAmount(bsViews));
                            bs.setCpc(calculateCpc(bs.getAmount_spent(), bsClicks));
                            bs.setCtr(calculateCtr(bsViews,bsClicks));
                            bs.setAvg_played_time(calculateAveragePercentage(bsPt25,bsPt50,bsPt75,bsPt100));
                            bs.setStatus(b.getBannerAd().isActive());
                        }


                    }

                }
                bannerAdStatIDs.add(b.getBannerAd().getId());
            }else{
                if (bannerAdStats.isEmpty() || !bannerAdStatIDs.contains(b.getBannerAd().getId())){
                    bannerAdStat.setBannerAdId(b.getBannerAd().getId());
                    bannerAdStat.setClicks(0);
                    bannerAdStat.setViews(0);
                    bannerAdStat.setClicksOnSkip(0);
                    bannerAdStat.setPt25(0);
                    bannerAdStat.setPt50(0);
                    bannerAdStat.setPt75(0);
                    bannerAdStat.setPt100(0);
                    bannerAdStat.setBannerName(b.getBannerAd().getBannerName());
                    bannerAdStat.setStatus(b.getBannerAd().isActive());
                    bannerAdStat.setType("Banner Ad");
                    bannerAdStats.add(bannerAdStat);
                }else {
                    for (BannerAdStat bs:bannerAdStats
                    ) {
                        int bsClicks= bs.getClicks();
                        int bsViews= bs.getViews();
                        int bsReach= b.getBannerAd().getReach();
                        int bsClicksOnSkip= bs.getClicksOnSkip();
                        int bsPt25= bs.getPt25();
                        int bsPt50= bs.getPt50();
                        int bsPt75= bs.getPt75();
                        int bsPt100= bs.getPt100();




                        if (bs.getBannerAdId().equals(b.getBannerAd().getId()) ){
                            bs.setClicks(bsClicks);
                            bs.setViews(bsViews);
                            bs.setReach(bsReach);
                            bs.setClicksOnSkip(bsClicksOnSkip);
                            bs.setPt25(bsPt25);
                            bs.setPt50(bsPt50);
                            bs.setPt75(bsPt75);
                            bs.setPt100(bsPt100);
                            bs.setAmount_spent(calculateAmount(bsViews));
                            bs.setCpc(calculateCpc(bs.getAmount_spent(), bsClicks));
                            bs.setCtr(calculateCtr(bsViews,bsClicks));
                            bs.setAvg_played_time(calculateAveragePercentage(bsPt25,bsPt50,bsPt75,bsPt100));
                            bs.setStatus(b.getBannerAd().isActive());
                        }


                    }

                }
                bannerAdStatIDs.add(b.getBannerAd().getId());
            }

        }
        return bannerAdStats;
    }
    List<BannerAdStat> calculateBannerAdStatsByUserGender(List<BannerAdImpression> bannerAdImpressions,String filter){
        List<BannerAdStat> bannerAdStats=new ArrayList<>();
        List<Long> bannerAdStatIDs=new ArrayList<>();

        for (BannerAdImpression b:bannerAdImpressions
        ) {
            BannerAdStat bannerAdStat=new BannerAdStat();
            Long userId=b.getUserId();
            User user=userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("user not found ! "));
            FilterInsights filterInsights= classifyUsersByGender(user);
            if (filterInsights==FilterInsights.valueOf(filter)){
                if (bannerAdStats.isEmpty() || !bannerAdStatIDs.contains(b.getBannerAd().getId())){
                    bannerAdStat.setBannerAdId(b.getBannerAd().getId());
                    bannerAdStat.setClicks(b.getClicks());
                    bannerAdStat.setViews(b.getViews());
                    bannerAdStat.setReach(b.getBannerAd().getReach());
                    bannerAdStat.setClicksOnSkip(b.getClicksOnSkip());
                    bannerAdStat.setPt25(b.getPt25());
                    bannerAdStat.setPt50(b.getPt50());
                    bannerAdStat.setPt75(b.getPt75());
                    bannerAdStat.setPt100(b.getPt100());
                    bannerAdStat.setAmount_spent(calculateAmount(b.getViews()));
                    bannerAdStat.setCpc(calculateCpc(bannerAdStat.getAmount_spent(), b.getClicks()));
                    bannerAdStat.setCtr(calculateCtr(b.getViews(),b.getClicks()));
                    bannerAdStat.setAvg_played_time(calculateAveragePercentage(b.getPt25(),b.getPt50(),b.getPt75(),b.getPt100()));
                    bannerAdStat.setBannerName(b.getBannerAd().getBannerName());
                    bannerAdStat.setStatus(b.getBannerAd().isActive());
                    bannerAdStat.setType("Banner Ad");
                    bannerAdStats.add(bannerAdStat);
                }else {
                    for (BannerAdStat bs:bannerAdStats
                    ) {
                        int bsClicks= bs.getClicks();
                        int bsViews= bs.getViews();
                        int bsReach= b.getBannerAd().getReach();
                        int bsClicksOnSkip= bs.getClicksOnSkip();
                        int bsPt25= bs.getPt25();
                        int bsPt50= bs.getPt50();
                        int bsPt75= bs.getPt75();
                        int bsPt100= bs.getPt100();


                        bsClicks+=b.getClicks();
                        bsViews+=b.getViews();
                        bsClicksOnSkip+=b.getClicksOnSkip();
                        bsPt25+=b.getPt25();
                        bsPt50+=b.getPt50();
                        bsPt75+=b.getPt75();
                        bsPt100+=b.getPt100();

                        if (bs.getBannerAdId().equals(b.getBannerAd().getId()) ){
                            bs.setClicks(bsClicks);
                            bs.setViews(bsViews);
                            bs.setReach(bsReach);
                            bs.setClicksOnSkip(bsClicksOnSkip);
                            bs.setPt25(bsPt25);
                            bs.setPt50(bsPt50);
                            bs.setPt75(bsPt75);
                            bs.setPt100(bsPt100);
                            bs.setAmount_spent(calculateAmount(bsViews));
                            bs.setCpc(calculateCpc(bs.getAmount_spent(), bsClicks));
                            bs.setCtr(calculateCtr(bsViews,bsClicks));
                            bs.setAvg_played_time(calculateAveragePercentage(bsPt25,bsPt50,bsPt75,bsPt100));
                            bs.setStatus(b.getBannerAd().isActive());
                        }

                    }

                }
                bannerAdStatIDs.add(b.getBannerAd().getId());
            }else{
                if (bannerAdStats.isEmpty() || !bannerAdStatIDs.contains(b.getBannerAd().getId())){
                    bannerAdStat.setBannerAdId(b.getBannerAd().getId());
                    bannerAdStat.setClicks(0);
                    bannerAdStat.setViews(0);
                    bannerAdStat.setReach(0);
                    bannerAdStat.setClicksOnSkip(0);
                    bannerAdStat.setPt25(0);
                    bannerAdStat.setPt50(0);
                    bannerAdStat.setPt75(0);
                    bannerAdStat.setPt100(0);
                    bannerAdStat.setBannerName(b.getBannerAd().getBannerName());
                    bannerAdStat.setStatus(b.getBannerAd().isActive());
                    bannerAdStat.setType("Banner Ad");
                    bannerAdStats.add(bannerAdStat);
                }else {
                    for (BannerAdStat bs:bannerAdStats
                    ) {
                        int bsClicks= bs.getClicks();
                        int bsViews= bs.getViews();
                        int bsReach= b.getBannerAd().getReach();
                        int bsClicksOnSkip= bs.getClicksOnSkip();
                        int bsPt25= bs.getPt25();
                        int bsPt50= bs.getPt50();
                        int bsPt75= bs.getPt75();
                        int bsPt100= bs.getPt100();



                        if (bs.getBannerAdId().equals(b.getBannerAd().getId()) ){
                            bs.setClicks(bsClicks);
                            bs.setViews(bsViews);
                            bs.setReach(bsReach);
                            bs.setClicksOnSkip(bsClicksOnSkip);
                            bs.setPt25(bsPt25);
                            bs.setPt50(bsPt50);
                            bs.setPt75(bsPt75);
                            bs.setPt100(bsPt100);
                            bs.setAmount_spent(calculateAmount(bsViews));
                            bs.setCpc(calculateCpc(bs.getAmount_spent(), bsClicks));
                            bs.setCtr(calculateCtr(bsViews,bsClicks));
                            bs.setAvg_played_time(calculateAveragePercentage(bsPt25,bsPt50,bsPt75,bsPt100));
                            bs.setStatus(b.getBannerAd().isActive());
                        }


                    }

                }
                bannerAdStatIDs.add(b.getBannerAd().getId());
            }

        }
        return bannerAdStats;
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
    public float calculateCpc(float amount, int click) {
        if (click != 0) {
            return Math.round(((amount / click) * 100) * 100) / 100.0f;
        }
        return 0;
    }

    public float calculateAmount(int views) {
        return Math.round((0.05F * views) * 100) / 100.0f;
    }

    public float calculateCtr(int view, int click) {
        if (view != 0) {
            return Math.round(((float) click / view) * 10000) / 100.0f;
        }
        return 0;
    }

    public static double calculateAveragePercentage(int pt25, int pt50, int pt75, int pt100) {
        int totalPlays = pt25 + pt50 + pt75 + pt100;
        if (totalPlays == 0) {
            return 0;
        }

        double totalPercentage = (0.25 * pt25) + (0.50 * pt50) + (0.75 * pt75) + (1.00 * pt100);
        return Math.round((totalPercentage / totalPlays) * 10000) / 100.0;
    }
}
