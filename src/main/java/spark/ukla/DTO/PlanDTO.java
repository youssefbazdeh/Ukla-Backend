package spark.ukla.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanDTO {
    Long Id;
    String Name;
    Boolean Followed;
    List<DayDTO> days;
    float calories ;

}
