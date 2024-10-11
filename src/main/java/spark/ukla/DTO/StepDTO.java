package spark.ukla.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.Video;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StepDTO {
    Long id;
    String instruction;
    String tip;
    @OneToOne
    Video video;
    @OneToMany(cascade = CascadeType.ALL)
    List<IngredientQuantityObjectDTO> ingredientQuantityObjects ;
}
