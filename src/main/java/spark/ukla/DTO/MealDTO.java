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
public class MealDTO {
    Long Id;
    String Name;
    List<RecipeCardDTO> recipes;
}
