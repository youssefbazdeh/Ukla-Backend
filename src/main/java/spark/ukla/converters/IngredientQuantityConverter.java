package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.IngredientQuantityObjectDTO;
import spark.ukla.entities.IngredientQuantityObject;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class IngredientQuantityConverter {
    private final IngredientConverter ingredientConverter;

    public IngredientQuantityConverter(IngredientConverter ingredientConverter) {
        this.ingredientConverter = ingredientConverter;
    }

    IngredientQuantityObjectDTO entityToDTO(IngredientQuantityObject ingredientQuantityDTO) {
        IngredientQuantityObjectDTO dto = new IngredientQuantityObjectDTO();
        dto.setId(ingredientQuantityDTO.getId());
        dto.setQuantity(ingredientQuantityDTO.getQuantity());
        dto.setIngredient(ingredientConverter.entityToDTO(ingredientQuantityDTO.getIngredient()));
        dto.setUnit(ingredientQuantityDTO.getUnit());
        return dto;
    }
    List<IngredientQuantityObjectDTO> entitiesToDTOS(List<IngredientQuantityObject> ingredientQuantityDTOS) {
        return ingredientQuantityDTOS.stream().map(x -> entityToDTO(x)).collect(Collectors.toList());
    }
}
