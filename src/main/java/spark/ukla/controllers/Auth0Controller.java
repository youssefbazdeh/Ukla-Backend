package spark.ukla.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.services.implementations.Auth0Service;
import spark.ukla.utils.SignInObject;

import java.io.IOException;
import java.security.GeneralSecurityException;


@RestController
@RequestMapping("/auth0")
public class Auth0Controller {

    @Autowired
    private Auth0Service auth0Service ;

    @PostMapping("/google-signin")
    public ResponseEntity signin (@RequestBody SignInObject signInObject ) throws GeneralSecurityException, IOException {
        String idtoken = signInObject.getIdtoken() ;

       return auth0Service.checkGoogleIdTokenIntegrityThenSigninOrSignup(idtoken);

        }

    @PostMapping("/google-signin/create_account")
    public ResponseEntity createAccount (@RequestBody SignInObject signInObject ) throws GeneralSecurityException, IOException {

        return auth0Service.createAccount(signInObject);

    }

    @PostMapping("/link-account/{password}")
    public ResponseEntity linkAccount(@RequestBody SignInObject signInObject,@PathVariable("password") String password) throws GeneralSecurityException, IOException {
        String idtoken = signInObject.getIdtoken();
        return auth0Service.linkAccount(idtoken,password);
    }



}




