package spark.ukla.foodIntakeEstimation.EstimationIngredientQuantity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.Image;
import spark.ukla.foodIntakeEstimation.EstimationIngredient.EstimationIngredient;
import spark.ukla.foodIntakeEstimation.EstimationRecipe.EstimationRecipe;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EstimationIngredientQuantity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    private Image image ;
    private int quantity ;

    @JsonIgnore
    @ManyToOne
    private EstimationIngredient estimationIngredient;
}
