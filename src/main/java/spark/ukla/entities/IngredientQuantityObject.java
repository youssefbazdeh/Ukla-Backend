package spark.ukla.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.enums.Unit;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientQuantityObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Column(nullable = false)

	Float quantity;


	@OneToOne()
	Ingredient ingredient;

	Unit unit;
}
