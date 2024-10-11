package spark.ukla.services.interfaces;

import spark.ukla.entities.groceyList.GroceryList;

import java.util.List;

public interface IGroceryListService {

    GroceryList retrieveGroceryList(String username,String languageCode,String countryCode) ;
    GroceryList getGroceryListDto(GroceryList groceryList);
    void deleteGroceryIngredientQuantityObject(List<Long> IdsList);
    void purchase(List<Long> IdsList);
    void unpurchase(List<Long> IdsList);
    void checkForUpdates(Long followedPlanId, GroceryList groceryList);
}
