package spark.ukla.ad_feature.campaign;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.ad_feature.bannerAdImpressions.BannerAdStat;
import spark.ukla.ad_feature.ingredientAdImpressions.IngredientAdStat;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CampaignStats {
    String campaignName;
    float budget;

    List<BannerAdStat> bannerAdStats;
    List<IngredientAdStat> ingredientAdStats;
}
