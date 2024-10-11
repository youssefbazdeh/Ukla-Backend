package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.StepDTO;
import spark.ukla.entities.Step;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StepConverter {
    private final IngredientQuantityConverter ingredientQuantityConverter;
    public StepConverter(IngredientQuantityConverter ingredientQuantityConverter){
        this.ingredientQuantityConverter = ingredientQuantityConverter;
    }
    StepDTO entityToDTO(Step step){
        StepDTO stepDTO = new StepDTO();
        stepDTO.setId(step.getId());
        stepDTO.setVideo(step.getVideo());
        stepDTO.setTip(step.getTip());
        stepDTO.setInstruction(step.getInstruction());
        stepDTO.setIngredientQuantityObjects(ingredientQuantityConverter.entitiesToDTOS(step.getIngredientQuantityObjects()));
        return  stepDTO;
    }

    List<StepDTO> entitiestoDTO (List<Step> steps){
        return steps.stream().map(x -> entityToDTO(x)).collect(Collectors.toList());
    }
}
