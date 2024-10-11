package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.ad_feature.CountryCode;
import spark.ukla.ad_feature.ingrediantAd.IngredientAd;
import spark.ukla.ad_feature.ingrediantAd.IngredientAdService;
import spark.ukla.entities.Ingredient;
import spark.ukla.entities.IngredientQuantityObject;
import spark.ukla.entities.groceyList.GroceryDay;
import spark.ukla.entities.groceyList.GroceryIngredientQuantityObject;
import spark.ukla.entities.groceyList.GroceryList;
import spark.ukla.entities.groceyList.GroceryRecipe;

import java.util.ArrayList;
import java.util.List;

@Component
public class GroceryListConverter {
    private final RecipeConverter recipeConverter;
    private final IngredientAdService ingredientAdService;
    public GroceryListConverter(RecipeConverter recipeConverter, IngredientAdService ingredientAdService) {
        this.recipeConverter = recipeConverter;
        this.ingredientAdService = ingredientAdService;
    }

    public List<Long> getIngredientIdsFromGroceryList(GroceryList groceryList){
        List<Long> ids = new ArrayList<>();
        for(GroceryDay groceryDay : groceryList.getGroceryDays()){
            for(GroceryRecipe groceryRecipe : groceryDay.getRecipes()){
                for(GroceryIngredientQuantityObject groceryIngredientQuantityObject : groceryRecipe.getGroceryIngredientQuantityObjects()){
                    IngredientQuantityObject ingredientQuantityObject = groceryIngredientQuantityObject.getIngredientQuantityObject();
                    ids.add(ingredientQuantityObject.getIngredient().getId());
                }
            }
        }
        return ids;
    }

    public GroceryList entityToTranslated(GroceryList groceryList,String languageCode,String countryCode){
        List<Long> ids = getIngredientIdsFromGroceryList(groceryList);
        List<IngredientAd> ingredientAds = ingredientAdService.getAllByIngredientIdsAndActiveAndCountryCode(ids, CountryCode.valueOf(countryCode));
        if(!ingredientAds.isEmpty()){
            for(GroceryDay groceryDay : groceryList.getGroceryDays()){
                for(GroceryRecipe groceryRecipe : groceryDay.getRecipes()){
                    for (GroceryIngredientQuantityObject groceryIngredientQuantityObject : groceryRecipe.getGroceryIngredientQuantityObjects()){
                        IngredientQuantityObject ingredientQuantityObject = groceryIngredientQuantityObject.getIngredientQuantityObject();
                        Ingredient ingredient = ingredientQuantityObject.getIngredient();
                        for(IngredientAd ingredientAd : ingredientAds){
                            if(ingredientAd.getIngredientId().equals(ingredient.getId())){
                                String translatedName = recipeConverter.getTranslatedOrDefaultName(ingredient,languageCode);
                                ingredient.setName(translatedName);
                                ingredient.setIngredientAd(ingredientAd);
                            }
                        }

                    }
                }
            }
        }
        else{
            for(GroceryDay groceryDay : groceryList.getGroceryDays()){
                for(GroceryRecipe groceryRecipe : groceryDay.getRecipes()){
                    for (GroceryIngredientQuantityObject groceryIngredientQuantityObject : groceryRecipe.getGroceryIngredientQuantityObjects()){
                        IngredientQuantityObject ingredientQuantityObject = groceryIngredientQuantityObject.getIngredientQuantityObject();
                        Ingredient ingredient = ingredientQuantityObject.getIngredient();
                        String translatedName = recipeConverter.getTranslatedOrDefaultName(ingredient,languageCode);
                        ingredient.setName(translatedName);
                    }
                }
            }
        }
        return groceryList;
    }
}
