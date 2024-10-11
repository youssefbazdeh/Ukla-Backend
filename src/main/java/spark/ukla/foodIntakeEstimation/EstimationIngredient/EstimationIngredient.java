package spark.ukla.foodIntakeEstimation.EstimationIngredient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.Image;
import spark.ukla.entities.enums.Unit;
import spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity.EstimationIngredientQuantity;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EstimationIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name ;
    private Unit unit;
   // private String image;

    @OneToOne
    private Image image;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    private List<EstimationIngredientQuantity> EstimationIngredientQuantities;

}
