package spark.ukla.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DayDTO {
    Long Id;
    String Name;
    LocalDate Date;
    List<MealDTO> meals;
}
