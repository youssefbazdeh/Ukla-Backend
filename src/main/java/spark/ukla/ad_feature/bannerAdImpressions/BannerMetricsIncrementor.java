package spark.ukla.ad_feature.bannerAdImpressions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerMetricsIncrementor {
    boolean click;
    boolean views;
    boolean clicksOnSkip;

    //Plays till 25, 50, 75, 100
    boolean pt25;
    boolean pt50;
    boolean pt75;
    boolean pt100;
}
