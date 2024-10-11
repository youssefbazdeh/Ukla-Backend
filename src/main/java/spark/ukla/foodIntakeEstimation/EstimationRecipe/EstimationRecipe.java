package spark.ukla.foodIntakeEstimation.EstimationRecipe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity.EstimationIngredientQuantity;
import spark.ukla.foodIntakeEstimation.EstimationMeal.EstimationMeal;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EstimationRecipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name ;
    private int frequency ;

    @ManyToOne
    @JsonIgnore
    EstimationMeal estimationMeal ;

    @ManyToMany(cascade = CascadeType.ALL)
    List<EstimationIngredientQuantity>estimationIngredientQuantities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstimationRecipe that = (EstimationRecipe) o;
        return id == that.id &&
                frequency == that.frequency &&
                Objects.equals(name, that.name) &&
                Objects.equals(estimationMeal, that.estimationMeal) &&
                Objects.equals(estimationIngredientQuantities, that.estimationIngredientQuantities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, frequency, estimationMeal, estimationIngredientQuantities);
    }
}
