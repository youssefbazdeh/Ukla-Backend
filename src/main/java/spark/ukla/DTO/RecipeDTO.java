package spark.ukla.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.creator_feature.Creator;
import spark.ukla.entities.*;
import spark.ukla.entities.enums.RecipeSeparation;
import spark.ukla.entities.enums.TypeRecipe;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RecipeDTO {
	Long id;
	@NotBlank(message = "Name required")
	String name;
	@NotBlank(message = "Description required")
	String description;
	@NotNull(message = "Preparation Time required")
	int preparationTime;
	@NotNull(message = "Cooking Time required")
	int cookingTime;
	@NotNull(message = "Type Recipe required")
	int portions;
	TypeRecipe type;
	List<RecipeSeparation> recipeSeparations;
	String toAvoid;
	String toRecommend;
	float calories;
	float protein;
	float fat;
	float carbs;
	float fiber;
	float sugar;
	boolean favorite;


	@OneToOne
	Video video;

	@NotNull(message = "Steps required")
	List<Step> steps;

	@OneToOne
	Image image;


	@ManyToMany(cascade = CascadeType.MERGE)
	Set<Tag> tags ;

	@ManyToOne
	Creator creator;
}
