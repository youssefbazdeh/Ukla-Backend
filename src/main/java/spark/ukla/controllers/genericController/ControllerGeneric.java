package spark.ukla.controllers.genericController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

public interface ControllerGeneric<T> {

    ResponseEntity<Object> save(@RequestBody T entity);

    ResponseEntity<T> findAll();

    ResponseEntity<Boolean> delete(@PathVariable Long id);

}
