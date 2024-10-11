package spark.ukla.repositories.projection;

import java.time.LocalDate;

public interface DayProjection {
    Long getId();
    String getName();
    LocalDate getDate();
}
