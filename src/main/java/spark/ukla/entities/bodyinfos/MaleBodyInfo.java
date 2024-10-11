package spark.ukla.entities.bodyinfos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class MaleBodyInfo extends BodyInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    float physicalActivityLevelF ; // number of hours
    @JsonIgnore
    @OneToOne
    User user ;




}
