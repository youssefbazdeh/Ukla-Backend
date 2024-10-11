package spark.ukla.creator_feature;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.DTO.RecipeCardDTO;
import spark.ukla.entities.Image;

import java.util.List;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatorDTOForProfile {
    Long id;
    String firstName;
    String lastName;
    String username;
    String description;
    Image image;
    List<RecipeCardDTO> createdRecipe;
    Boolean followed = false;
    int followersNumber;
}
