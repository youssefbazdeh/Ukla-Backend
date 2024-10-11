package spark.ukla.services.interfaces;

import spark.ukla.entities.Allergy;
import spark.ukla.entities.Image;

import java.util.List;

public interface IAllergiesService {

    Boolean add(String name, List<Long> IngredientIds, Image image);
    List<Allergy> getAll();
    List<Allergy> getAllByUserId(String username);
    Boolean addAllergiesIds(List<Long> ids, String username);
    boolean delete(Long id);
    Boolean update(Allergy allergy,List<Long> IngredientIds, Image image);
    Boolean updateImage(Image image,long id);
    Boolean deleteAllergiesByUserId(Long id, String username);
    Boolean existsByName(String name);
}
