package spark.ukla.services.implementations;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.entities.Advice;
import spark.ukla.repositories.AdviceRepository;
import spark.ukla.services.interfaces.IAdviceService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdviceService implements IAdviceService {

    @Autowired
    AdviceRepository adviceRepository;


    @Override
    public String addAdvice(Advice advice) {
        String msg = "";
            adviceRepository.save(advice);
            msg = "Advice saved.";

        return msg;
    }
    @Override
    public String update(Advice advice) {
        String msg="";
        Boolean Exists = adviceRepository.existsById(advice.getId());
        if(Exists){
            adviceRepository.update(advice.getId(),advice.getText());
            msg = "Advice updated.";
        }else
            msg = "Advice not found.";
        return msg;
    }

    @Override
    public void delete(Long idAdvice) {
        adviceRepository.deleteAdviceById(idAdvice);
    }

    @Override
    public List<Advice> retrieveAll() {
        return adviceRepository.findAll();
    }

    @Override
    public Optional<Advice> retrieveById(Long id) {
        Boolean ExistsById = adviceRepository.existsById(id);
        if (ExistsById)
            return adviceRepository.findById(id);
        else
            return null;
    }
}
