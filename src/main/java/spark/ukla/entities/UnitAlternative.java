package spark.ukla.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.enums.Unit;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitAlternative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated
    Unit unit ;
    double conversionRate;

}
