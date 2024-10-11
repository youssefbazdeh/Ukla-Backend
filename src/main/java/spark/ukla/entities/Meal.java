package spark.ukla.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


//@Table(name = "Meal", uniqueConstraints = { @UniqueConstraint(name = "Meal_name_unique", columnNames = "name") })
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Meal  implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column( nullable = false)

    String name;

    @ManyToMany(cascade = CascadeType.DETACH)

    private List<Recipe> recipes;

    @ManyToOne(fetch= FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JsonIgnore
    Day day;
}
