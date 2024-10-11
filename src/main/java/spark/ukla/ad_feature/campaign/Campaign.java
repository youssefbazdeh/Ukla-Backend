package spark.ukla.ad_feature.campaign;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.ad_feature.bannerAd.BannerAd;
import spark.ukla.ad_feature.client.Client;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.ad_feature.ingrediantAd.IngredientAd;

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
public class Campaign implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String campaignName;
    int viewsObjective;
    boolean active;
    float budget;

    @Enumerated(EnumType.STRING)
    CountryCode countryCode;

    @OneToMany
    List<IngredientAd> ingredientAds;

    @OneToMany
    List<BannerAd> bannerAds;

    @ManyToOne
    Client client;
}
