package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.IngredientDTO;
import spark.ukla.entities.Ingredient;
import spark.ukla.entities.TranslatedIngredient;

import java.util.ArrayList;
import java.util.List;

@Component
public class IngredientConverter {
    IngredientDTO entityToDTO(Ingredient ingredient) {
        IngredientDTO dto = new IngredientDTO();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setType(ingredient.getType());
        dto.setImage(ingredient.getImage());
        dto.setUnitAlternatives(ingredient.getUnitAlternatives());
        dto.setIngredientAd(ingredient.getIngredientAd());
        return dto;
    }


    public String getTranslatedOrDefaultName(Ingredient ingredient, String languageCode) {
        List<TranslatedIngredient> translatedIngredients = ingredient.getTranslatedIngredients();
        for (TranslatedIngredient translatedIngredient : translatedIngredients) {
            if (translatedIngredient.getLanguageCode().equals(languageCode)) {
                return translatedIngredient.getName();
            }
        }
        return ingredient.getName();
    }

    public List<IngredientDTO> entityToIngredientDTOTranslated(List<Ingredient> ingredient, String languageCode){
        List<IngredientDTO> ingredientDTOS = new ArrayList<>();

            for (Ingredient i: ingredient){
                String translatedName = getTranslatedOrDefaultName(i,languageCode);
                IngredientDTO ingredientDTO = new IngredientDTO();
                ingredientDTO.setName(translatedName);
                ingredientDTO.setId(i.getId());
                ingredientDTO.setType(i.getType());
                ingredientDTO.setImage(i.getImage());
                ingredientDTO.setUnitAlternatives(i.getUnitAlternatives());
                ingredientDTOS.add(ingredientDTO);

            }

        return ingredientDTOS;
    }


}
