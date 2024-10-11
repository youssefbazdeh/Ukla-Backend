
package spark.ukla.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Day implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	@Column(name = "name")
	//@NotBlank(message = "Name required")
	String name;
	@Column(name = "DATE", nullable = false)
	//@NotBlank(message = "Date required")
	//@Temporal(TemporalType.DATE)
	LocalDate date;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JsonIgnore
	PlanOfWeek planOfWeek;

	@OneToMany(mappedBy ="day",cascade = CascadeType.ALL)

	List<Meal> meals;


}
