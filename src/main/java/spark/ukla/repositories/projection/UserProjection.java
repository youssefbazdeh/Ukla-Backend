package spark.ukla.repositories.projection;

import spark.ukla.entities.enums.Gender;

public interface UserProjection {
    Long getId();
    Gender getGender();
}
