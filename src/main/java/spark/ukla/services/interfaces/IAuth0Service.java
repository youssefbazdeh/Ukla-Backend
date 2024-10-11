package spark.ukla.services.interfaces;


import org.springframework.http.ResponseEntity;
import spark.ukla.utils.SignInObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface IAuth0Service {
    ResponseEntity checkGoogleIdTokenIntegrityThenSigninOrSignup(String idtoken) throws GeneralSecurityException, IOException;
    ResponseEntity   createAccount (SignInObject signInObject) throws GeneralSecurityException, IOException;
    ResponseEntity linkAccount(String idtoken,String password)throws GeneralSecurityException, IOException;

}
