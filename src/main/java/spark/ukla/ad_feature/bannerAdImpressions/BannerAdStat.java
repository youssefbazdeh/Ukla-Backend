package spark.ukla.ad_feature.bannerAdImpressions;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerAdStat {  //Object to gather the data to send it with campaign stats
    Long bannerAdId;
    String bannerName;
    String type;
    boolean status;
    int views;
    int reach;
    int clicks;
    int clicksOnSkip;

    //Plays till 25, 50, 75, 100
    int pt25;
    int pt50;
    int pt75;
    int pt100;

    float cpc;
    float ctr;
    float amount_spent;

    //Average of Played time
    double avg_played_time;

}
