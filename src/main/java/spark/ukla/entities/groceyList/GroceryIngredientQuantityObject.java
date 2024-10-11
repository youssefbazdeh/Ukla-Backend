package spark.ukla.entities.groceyList;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.IngredientQuantityObject;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroceryIngredientQuantityObject implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    @OneToOne(cascade = CascadeType.DETACH)
    IngredientQuantityObject ingredientQuantityObject ;


    Boolean purchased=false ;


}
