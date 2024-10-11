package spark.ukla.creator_feature;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.Image;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatorDTOForRecipe {
    Long id;
    String description;
    Image image;
    String firstName;
    String lastName;
}
