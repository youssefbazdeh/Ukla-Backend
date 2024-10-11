package spark.ukla.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.Image;
import spark.ukla.entities.Tag;
import spark.ukla.entities.enums.Status;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeCardDTO {
    Long id;
    String name;
    int cookingTime;
    int preparationTime;
    float calories;
    @Enumerated
    Status status;
    @OneToOne
    Image image;
    @ManyToMany(cascade = CascadeType.MERGE)
    Set<Tag> tags ;
    boolean isRecipeInUserFavorites;
}
