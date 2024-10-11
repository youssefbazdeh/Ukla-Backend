package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.PlanDTO;
import spark.ukla.entities.PlanOfWeek;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlanConverter {
    private final DayConverter dayConverter;

    public PlanConverter(DayConverter  dayConverter){this.dayConverter = dayConverter;}
    public PlanDTO entityToDTO(PlanOfWeek plan){
        PlanDTO planDTO = new PlanDTO();
        planDTO.setId(plan.getId());
        planDTO.setName(plan.getName());
        planDTO.setFollowed(plan.getFollowed());
        planDTO.setDays(dayConverter.entitiesToDTO(plan.getDays()));
        planDTO.setCalories(plan.getCalories());
        return planDTO;
    }


    public List<PlanDTO> entitesToDTO(List<PlanOfWeek> plan){
        return plan.stream().map(x -> entityToDTO(x)).collect(Collectors.toList());
    }

}
