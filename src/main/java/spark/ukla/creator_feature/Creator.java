package spark.ukla.creator_feature;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.Image;
import spark.ukla.entities.Recipe;
import spark.ukla.entities.User;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Creator extends User {


    String description;
    boolean verified;
    int followersNumber;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Recipe> createdRecipe;

    @ManyToMany
    List<User> followers ;
    @OneToOne
    Image image;

    public void incrementFollowers() {
        this.followersNumber++;
    }
    public void decrementFollowers() {
        this.followersNumber--;
    }

}
