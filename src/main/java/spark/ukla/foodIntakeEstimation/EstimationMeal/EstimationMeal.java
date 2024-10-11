package spark.ukla.foodIntakeEstimation.EstimationMeal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.User;
import spark.ukla.foodIntakeEstimation.EstimationRecipe.EstimationRecipe;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EstimationMeal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank(message = "name required")
    String name ;

    Boolean filled = false ;
    @OneToMany(mappedBy = "estimationMeal")
    List<EstimationRecipe> estimationRecipes= new ArrayList<>() ;

    @JsonIgnore
    @ManyToOne
    User user ;
}
