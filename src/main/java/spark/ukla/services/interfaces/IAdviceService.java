package spark.ukla.services.interfaces;

import spark.ukla.entities.Advice;
import spark.ukla.services.generic.ServiceGeneric;

import java.util.List;
import java.util.Optional;

public interface IAdviceService {

    String addAdvice(Advice advice);

    String update(Advice advice);

    void delete(Long idAdvice);

    List<Advice> retrieveAll();

    Optional<Advice> retrieveById(Long id);
}
