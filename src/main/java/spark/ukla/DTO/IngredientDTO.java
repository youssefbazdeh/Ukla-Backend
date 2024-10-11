package spark.ukla.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.ad_feature.ingrediantAd.IngredientAd;
import spark.ukla.entities.Image;
import spark.ukla.entities.TranslatedIngredient;
import spark.ukla.entities.UnitAlternative;
import spark.ukla.entities.enums.TypeIngredient;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientDTO {

    Long id;

    @NotBlank(message = "Name required")
    String name;

    @NotNull(message = "Type required")
    @Enumerated(EnumType.STRING)
    TypeIngredient type;

    @OneToMany
    List<UnitAlternative> unitAlternatives ;
    @OneToOne
    Image image;
    @Transient
    IngredientAd ingredientAd;
}
