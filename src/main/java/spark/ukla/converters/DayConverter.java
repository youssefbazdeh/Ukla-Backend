package spark.ukla.converters;

import org.springframework.stereotype.Component;
import spark.ukla.DTO.DayDTO;
import spark.ukla.entities.Day;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DayConverter {
    private final MealConverter mealConverter;

    public DayConverter(MealConverter mealConverter){
        this.mealConverter = mealConverter;
    }

    public DayDTO entityToDTO(Day day){
        DayDTO dayDTO = new DayDTO();
        dayDTO.setId(day.getId());
        dayDTO.setName(day.getName());
        dayDTO.setDate(day.getDate());
        dayDTO.setMeals(mealConverter.entitiesToDTO(day.getMeals()));
        return dayDTO;
    }

    public List<DayDTO> entitiesToDTO(List<Day> days){
        return days.stream().map(x -> entityToDTO(x)).collect(Collectors.toList());
    }

}
