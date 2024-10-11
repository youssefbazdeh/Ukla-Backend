package spark.ukla.repositories.projection;

import spark.ukla.entities.Image;

public interface CreatorProjection {
    Long getId();
    String getFirstName();
    String getDescription();
    String getLastName();
    Image getImage();

}
