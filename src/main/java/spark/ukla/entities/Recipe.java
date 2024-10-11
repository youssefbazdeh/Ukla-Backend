package spark.ukla.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.creator_feature.Creator;
import spark.ukla.entities.enums.Status;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Table(name = "Recipe")
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Recipe implements Serializable {
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
	float calories;
	float protein;
	float fat;
	float carbs;
	float fiber;
	float sugar;
	int portions;
	int views = 0;
	@Transient
	boolean favorite;

	@Enumerated
	Status status;
	@OneToOne(cascade = CascadeType.ALL)
	Image image;

	@OneToOne(cascade = CascadeType.ALL)
	Video video;



	@OneToMany(cascade = CascadeType.ALL)
	@NotNull(message = "steps required")
	List<Step> steps;


	@ManyToMany(cascade = CascadeType.MERGE)
	Set<Tag> tags ;

	@ManyToOne
	@JsonIgnore
	Creator creator;

	public void incrementViews() {
		this.views++;
	}

	}
