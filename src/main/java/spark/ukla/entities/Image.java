package spark.ukla.entities;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
@Data
public class Image {

    @Id
    @GeneratedValue
    Long id;

    String location;


    public Image( String location) {
        this.location = location;
    }

    public Image() {
    }

}
