package spark.ukla.creator_recipe;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.Video;
import spark.ukla.entities.enums.CreatorRecipeStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatorRecipe implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    @OneToOne(cascade = CascadeType.ALL)
    Video video;
    String description;
    String creator;

    @Enumerated
    CreatorRecipeStatus status;
}
