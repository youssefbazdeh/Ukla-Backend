package spark.ukla.repositories.projection;

import spark.ukla.entities.*;

import java.util.List;
import java.util.Set;

public interface ViewRecipeProjection {
    Long getId();
    String getName();
    String getDescription();
    int getPreparationTime();
    int getCookingTime();
    int getPortions();
    float getCalories();
    float getProtein();
    float getFat();
    float getCarbs();
    float getFiber();
    float getSugar();
    List<Step> getSteps();
    boolean isFavorite();
    Video getVideo();
    Image getImage();
    Set<Tag> getTags();
    CreatorProjection getCreator();
}
