package spark.ukla.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.creator_feature.CreatorDTOForRecipe;
import spark.ukla.entities.Image;
import spark.ukla.entities.Tag;
import spark.ukla.entities.Video;
import spark.ukla.entities.enums.RecipeSeparation;
import spark.ukla.entities.enums.TypeRecipe;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewRecipeDTO {
    Long id;
    @NotBlank(message = "Name required")
    String name;
    @NotBlank(message = "Description required")
    String description;
    @NotNull(message = "Preparation Time required")
    int preparationTime;
    @NotNull(message = "Cooking Time required")
    int cookingTime;
    @NotNull(message = "Type Recipe required")
    TypeRecipe type;
    int portions;
    List<RecipeSeparation> recipeSeparations;
    String toAvoid;
    String toRecommend;
    float calories;
    float protein;
    float fat;
    float carbs;
    float fiber;
    float sugar;
    boolean favorite;


    @OneToOne
    Video video;

    @NotNull(message = "Steps required")
    List<StepDTO> steps;

    @OneToOne
    Image image;


    @ManyToMany(cascade = CascadeType.MERGE)
    Set<Tag> tags ;

    @ManyToOne
    CreatorDTOForRecipe creator;
}
