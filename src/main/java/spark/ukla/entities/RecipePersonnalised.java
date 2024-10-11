package spark.ukla.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.enums.RecipeSeparation;
import spark.ukla.entities.enums.TypeRecipe;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Table(name = "RecipePersonnalised", uniqueConstraints = { @UniqueConstraint(name = "Recipe_Personalised_name_unique", columnNames = "name") })
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipePersonnalised implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "NAME", nullable = false)
    @NotBlank(message = "Name required")
    String name;
    @Column(name = "DESCRIPTION", columnDefinition = "Text", nullable = false)
    @NotBlank(message = "Description required")
    String description;
    @Column(name = "PREPARATION_TIME", nullable = false)
    @NotNull(message = "Preparation Time required")
    int preparationTime;
    @Column(name = "COOKING_TIME", nullable = false)
    @NotNull(message = "Cooking Time required")
    int cookingTime;
    @Column(name = "TYPE", nullable = false)
    @NotNull(message = "Type Recipe required")
    @Enumerated(EnumType.STRING)
    TypeRecipe type;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    List<RecipeSeparation> recipeSeparations;

    @Column(name = "TO_AVOID", nullable = false)
    String toAvoid;
    @Column(name = "TO_RECOMMEND", nullable = false)
    String toRecommend;
    @Column(name = "NUMBER_OF_CALORIES", nullable = false)
    float nbrCalories;

    String location;

    @OneToOne
    Image image;

    @OneToMany()
    Set<IngredientQuantityObject> ingredientQuantityObjects;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JsonIgnore
    Meal meal;

    @OneToMany
    @NotNull(message = "steps required")
    Set<Step> steps;


    public RecipePersonnalised(String name, String location) {
        this.name = name;
        this.location = location;
    }
}
