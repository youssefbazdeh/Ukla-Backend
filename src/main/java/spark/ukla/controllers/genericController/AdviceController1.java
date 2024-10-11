package spark.ukla.controllers.genericController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spark.ukla.entities.Advice;
import spark.ukla.repositories.AdviceRepository;
import spark.ukla.services.generic.ServiceGeneric;

@RestController
@RequestMapping(path = "/advice")
public class AdviceController1 extends ControllerGenericImpl<Advice> implements AdviceController{

    @Autowired
    AdviceRepository adviceRepository;

    public AdviceController1(ServiceGeneric<Advice> genericService) {
        super(genericService);
    }

    @Override
    @GetMapping("{id}")
    public ResponseEntity findById(@PathVariable Long id) {
        return new ResponseEntity(adviceRepository.findById(id),HttpStatus.OK);
    }
}
