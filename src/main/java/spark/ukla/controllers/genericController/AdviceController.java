package spark.ukla.controllers.genericController;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import spark.ukla.entities.Advice;

public interface AdviceController extends ControllerGeneric<Advice>{

    ResponseEntity<Advice> findById(@PathVariable Long id);
}
