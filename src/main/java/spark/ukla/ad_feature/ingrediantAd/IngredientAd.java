package spark.ukla.ad_feature.ingrediantAd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.ad_feature.ingredientAdImpressions.IngredientAdImpression;
import spark.ukla.entities.Image;

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
public class IngredientAd implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String brandName;
    boolean active;
    @Nullable
    int views;
    Long ingredientId;
    @Nullable
    float amountSpent;
    @Nullable
    float views_rate;
    @Nullable
    float frequency;
    @Nullable
    int reach;

    @Enumerated
    CountryCode countryCode;

    @OneToOne
    Image image;

    @JsonIgnore
    @OneToMany
    List<IngredientAdImpression> ingredientAdImpressions ;
}
