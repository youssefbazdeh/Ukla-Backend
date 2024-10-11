package spark.ukla.entities.groceyList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.IngredientQuantityObject;
import spark.ukla.entities.User;

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
public class GroceryList implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @JsonIgnore
    @OneToOne
    User user ;
    @JsonIgnore
    Long planOfWeekId;

    @OneToMany(cascade = CascadeType.ALL)
    List<GroceryDay> groceryDays;

    @OneToMany(cascade=CascadeType.ALL )
    List<IngredientQuantityObject> userAddedIngredientQuantityObjects;



}
