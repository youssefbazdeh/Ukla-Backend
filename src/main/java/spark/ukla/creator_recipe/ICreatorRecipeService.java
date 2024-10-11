package spark.ukla.creator_recipe;

import spark.ukla.entities.Video;
import java.util.List;

public interface ICreatorRecipeService {
    CreatorRecipe add(String title, Video video, String username, String description);
    Boolean updateRecipeStatus(Long id, String status);
    List<CreatorRecipe> getAllByCreatorUsername(String username);
    List<CreatorRecipe> getAll(int page, int size);

    CreatorRecipe getbyid(Long id);
    int deleteCreatorRecipeById(long id);
    CreatorRecipe updateCreatorRecipe(Long id,String title,Video video, String description);
}
