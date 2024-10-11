package spark.ukla.ad_feature.bannerAdImpressions;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.ad_feature.bannerAd.BannerAd;

import java.util.List;

@Repository
public interface BannerAdImpressionRepository extends JpaRepository<BannerAdImpression, Long> {
    List<BannerAdImpression> findByBannerAd(BannerAd bannerAd);

    BannerAdImpression findBannerAdImpressionByBannerAdAndAndUserId(BannerAd bannerAd,long userId);

    boolean existsByUserIdAndAndBannerAd(long userId,BannerAd bannerAd);
}
