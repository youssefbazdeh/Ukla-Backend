package spark.ukla.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.IngredientQuantityObject;
import spark.ukla.entities.Step;
import spark.ukla.entities.enums.RecipeSeparation;
import spark.ukla.entities.enums.TypeRecipe;

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
public class RecipePersonnalisedDTO {
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
    List<RecipeSeparation> recipeSeparations;
    String toAvoid;
    String toRecommend;
    float nbrCalories;
    String location;


    @NotNull(message = "IngredientQuantity required")
    Set<IngredientQuantityObject> ingredientQuantityObjects;

    @NotNull(message = "Steps required")
    Set<Step> steps;
}
