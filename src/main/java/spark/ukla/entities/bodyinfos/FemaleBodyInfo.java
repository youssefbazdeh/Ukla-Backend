package spark.ukla.entities.bodyinfos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class FemaleBodyInfo extends BodyInfo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Boolean pregnant ;
    Date pregnancyDate ; // we ask How many weeks pregnant are they then convert it to a date

    @JsonIgnore
    @OneToOne
    User user ;

}
