package spark.ukla.services.interfaces;


import org.springframework.data.domain.Pageable;
import spark.ukla.entities.PlanOfWeek;
import spark.ukla.repositories.projection.PlanOfWeekProjection;


import java.time.LocalDate;
import java.util.List;

public interface IPlanOfWeek {



    PlanOfWeek addPlan(LocalDate userDate,String username);

    boolean deleteById(Long id);

    String update(PlanOfWeek planOfWeek);

    List<PlanOfWeekProjection> retrieveAllBYUser(String username, Pageable pageable);

    PlanOfWeek retrieveByName(String name);

    Boolean  followPlan (Long idPlan, String username) ;


    PlanOfWeek retrieveById(Long idPlan);


    String renamePlan(String newPlanName , long id);




}
