package spark.ukla.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spark.ukla.entities.waitList;
import spark.ukla.repositories.waitRepository;
@Service
public class waitService {
    @Autowired
    waitRepository waitrepository;


    public Boolean existsByName(String email) {
        return waitrepository.existsByEmail(email) ;
    }
    public void add(waitList wait) {
        waitrepository.save(wait);

    }
}
