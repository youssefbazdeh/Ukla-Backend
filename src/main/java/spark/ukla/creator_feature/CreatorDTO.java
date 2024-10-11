package spark.ukla.creator_feature;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.DTO.RecipeCardDTO;
import spark.ukla.DTO.UserDTO;
import spark.ukla.entities.Image;
import spark.ukla.entities.Recipe;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatorDTO extends UserDTO {
    String description;
    Image image;
    @JsonIgnore
    Set<Recipe> createdRecipe;
}
