package spark.ukla.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.ad_feature.ingrediantAd.IngredientAd;
import spark.ukla.entities.enums.TypeIngredient;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Table(uniqueConstraints = {
		@UniqueConstraint(name = "Ingredient_name_unique", columnNames = "name") })
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Ingredient implements Serializable {
	/**
	 * 
	 */
	 static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)
	@NotBlank(message = "Name required")
	String name;
	@Column( nullable = false)
	@NotNull(message = "Type required")
	@Enumerated(EnumType.STRING)
	TypeIngredient type;

	@OneToMany
	List<TranslatedIngredient> translatedIngredients;

	@Column(name = "NUMBER_OF_CALORIES", nullable = false)
	@NotNull(message = "Number of calories /100g required")
	float nbrCalories100gr;
	// macro elements in g/100 g
	float fat;
	float protein;
	float carbs;
	float sugar;
	float fiber;
	float saturatedFattyAcids;
	// macro elements in mg/100 g
	float cholesterol ;

	// micro elements in mg/100 g
	float calcium;
	float magnesium;
	float sodium ;
	float zinc;
	float iron;


	@OneToOne(cascade = CascadeType.ALL)
	Image image;

	@OneToMany
	List<UnitAlternative> unitAlternatives ;
	@ManyToMany
	List<Allergy> allergies;


	@Transient
	IngredientAd ingredientAd;


}
