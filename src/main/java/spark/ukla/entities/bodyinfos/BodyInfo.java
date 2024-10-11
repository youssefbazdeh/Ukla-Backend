package spark.ukla.entities.bodyinfos;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BodyInfo {


    float height ; // in cm
    float weight ; // in kg
    int age ;
    float physicalActivityLevelA ; // number of hours
    float physicalActivityLevelB ; // number of hours
    float physicalActivityLevelC ; // number of hours
    float physicalActivityLevelD ; // number of hours
    float physicalActivityLevelE ; // number of hours
    int calorieNeed ;


}
