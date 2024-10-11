package spark.ukla.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.enums.Unit;

import javax.persistence.CascadeType;
import javax.persistence.OneToOne;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class IngredientQuantityObjectDTO {

    Long id;

    Float quantity;

    @OneToOne(cascade= CascadeType.ALL)
    IngredientDTO ingredient;

    Unit unit;
}
