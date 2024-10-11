package spark.ukla.services.implementations;

import spark.ukla.entities.IngredientQuantityObject;
import spark.ukla.repositories.IngredientQuantityObjectRepository;
import spark.ukla.services.interfaces.IIngredientQuantityObjectService;

public class IngredientQuantityObjectService implements IIngredientQuantityObjectService {

    IngredientQuantityObjectRepository ingredientQuantityObjectRepository ;
    @Override
    public IngredientQuantityObject add(IngredientQuantityObject iIngredientQuantityObject) {
        return  ingredientQuantityObjectRepository.save(iIngredientQuantityObject)  ;
    }
}
