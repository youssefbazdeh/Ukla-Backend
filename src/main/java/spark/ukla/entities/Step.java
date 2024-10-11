package spark.ukla.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Step implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false,columnDefinition = "longText")
    @NotBlank(message = "Step required")
    String instruction;

    String tip;

    @OneToOne
    Video video;

    @OneToMany(cascade = CascadeType.ALL)

    List<IngredientQuantityObject> ingredientQuantityObjects ;
}
