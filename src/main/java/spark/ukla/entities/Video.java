package spark.ukla.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String location;

    private String sasUrl;

    public Video( String location) {

        this.location = location;
    }

    public Video(String location,String sasUrl){
        this.location = location;
        this.sasUrl = sasUrl;
    }


}
