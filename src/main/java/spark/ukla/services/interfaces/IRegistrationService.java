package spark.ukla.services.interfaces;


import org.springframework.http.ResponseEntity;
import spark.ukla.creator_feature.Creator;
import spark.ukla.entities.Image;
import spark.ukla.entities.User;

public interface IRegistrationService {

	ResponseEntity<String>register(User userDTO);

    String registerCreator(Creator newCreator, Image image);



    int enableUser(long id);
	
	String confirmToken(String token);
	String resendActivationCode(String username) ;

}
