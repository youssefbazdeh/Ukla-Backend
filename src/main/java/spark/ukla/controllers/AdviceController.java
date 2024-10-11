package spark.ukla.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import spark.ukla.entities.Advice;
import spark.ukla.services.interfaces.IAdviceService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/Advice")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdviceController {


    @Autowired
    IAdviceService iAdviceService;


    @GetMapping("/hello")

    public String helloAzure(){
        return "hello azure from spring boot";
    }

    @PostMapping("/add")
    @Transactional
    public String addAdvice(@RequestBody Advice advice){
        return iAdviceService.addAdvice(advice);
    }
    @PutMapping("/update")
    public String update(@RequestBody Advice advice){
        return iAdviceService.update(advice);
    }
    @DeleteMapping("/deleteById/{idAdvice}")
    public void deletee(@PathVariable Long idAdvice){
        iAdviceService.delete(idAdvice);
    }
    @GetMapping("/retrieveById/{id}")
    public Optional<Advice> retrieveById(@PathVariable Long id) {
        return iAdviceService.retrieveById(id);
    }
    @GetMapping("/retrieveAll")
    public List<Advice> retrieveAll() {
    return iAdviceService.retrieveAll();
    }
}
