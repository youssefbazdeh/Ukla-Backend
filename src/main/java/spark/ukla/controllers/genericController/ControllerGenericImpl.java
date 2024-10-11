package spark.ukla.controllers.genericController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.entities.Tag;
import spark.ukla.repositories.TagRepository;
import spark.ukla.services.generic.ServiceGeneric;
import spark.ukla.services.implementations.TagService;


@ResponseBody
public class ControllerGenericImpl<T> implements ControllerGeneric<T>{



    private final ServiceGeneric<T> genericService;

    public ControllerGenericImpl(ServiceGeneric<T> genericService) {
        this.genericService = genericService;
    }

    @Override
    @PostMapping("add")
    public ResponseEntity<Object> save(T entity) {
        try {
            return new ResponseEntity(genericService.save(entity),HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @GetMapping("getAll")
    public ResponseEntity<T> findAll() {
        try {
            return new ResponseEntity(genericService.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @DeleteMapping("deleteById/{id}")
    public ResponseEntity<Boolean> delete( @PathVariable Long id) {
        try {


            genericService.delete(id);
            return new ResponseEntity("Success!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("Error!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
