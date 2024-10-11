package spark.ukla.services.implementations;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spark.ukla.services.interfaces.IProfileService;

@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileService implements IProfileService {


}
