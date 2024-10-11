package spark.ukla.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Allergy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    String name;

    @ManyToMany
    @JsonIgnore
    private List<Ingredient> ingredients;

    @ManyToMany
    @JsonIgnore
    private List<User> users;

    @OneToOne
    private Image image;

    @ManyToMany
    @JsonIgnore
    private List<Recipe> recipes;

}
