package spark.ukla.entities.groceyList;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.Image;
import spark.ukla.entities.Recipe;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroceryRecipe implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonIgnore
    @OneToOne()
    Recipe recipe;

    @Transient String name;


    @Transient Image image;


    @OneToMany(cascade =CascadeType.ALL)

    List<GroceryIngredientQuantityObject> groceryIngredientQuantityObjects ;

}