package spark.ukla.ad_feature.ingredientAdImpressions;

import lombok.*;
import lombok.experimental.FieldDefaults;
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
public class IngredientAdImpression implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    long userId;
    int views;
    int impressions;

    @ManyToOne(cascade = CascadeType.ALL)
    IngredientAd ingredientAd ;
}
