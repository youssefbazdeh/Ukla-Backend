package spark.ukla.repositories.projection;


import java.util.List;

public interface PlanOfWeekProjection {
    String getName();
    Long getId();
    Boolean getFollowed();
    List<DayProjection> getDays();
}
