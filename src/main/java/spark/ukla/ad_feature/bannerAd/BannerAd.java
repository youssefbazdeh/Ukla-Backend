package spark.ukla.ad_feature.bannerAd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.ad_feature.bannerAdImpressions.BannerAdImpression;
import spark.ukla.entities.Image;
import spark.ukla.entities.Video;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BannerAd implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    boolean active;
    String redirectionLink;
    String bannerName;
    @Nullable
    float amountSpent;
    @Nullable
    float cpc;
    @Nullable
    float ctr;
    @Nullable
    float frequency;
    @Nullable
    int reach;
    @Nullable
    int clicks;
    @Nullable
    int views;
    @Nullable
    float averagePlayedDuration;

    //percentage views till 25, 50, 75, 100
    @Nullable
    float pcTill25;
    @Nullable
    float pcTill50;
    @Nullable
    float pcTill75;
    @Nullable
    float pcTill100;



    @Enumerated
    CountryCode countryCode;

    @OneToOne
    Video video;

    @OneToOne
    Image image;

    @JsonIgnore
    @OneToMany
    List<BannerAdImpression> bannerAdImpressions ;



}
