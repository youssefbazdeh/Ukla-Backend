package spark.ukla.ad_feature.ingredientAdImpressions;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientAdStat { //Object to gather the data to send it with campaign stats
    Long ingredientAdId;
    //List<Long> userIds;
    int views;
    int reach;
    int impressions;
    String Name;
    String type;
    float view_rate;
    float frequency;
    float amount_spent;
    boolean status;


}
