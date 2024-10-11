package spark.ukla.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.implementations.UserService;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequestMapping("/mail")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailController {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;


    @PostMapping("/sendCodeForUpdateEmail")
    public ResponseEntity<String> sendCodeForUpdateEmail(@RequestHeader("AUTHORIZATION") String header, @Param("email") String email) {
        String username = userService.getusernamefromtoken(header);
        String msg = "";
        if (userRepository.existsByEmail(email)) {
            msg = "email already used";
        } else if (userService.sendResetEmailToken(username, email)) {
            msg = "reset email token sent";

        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @PutMapping("/updateNewEmail")
    public ResponseEntity<String> updateNewEmail(@Param("token") String token,@Param("email")  String email){
        String msg = userService.updateNewEmail(token,email);
        return new ResponseEntity<>(msg,HttpStatus.OK);
    }

    @PostMapping("/sendResetEmailToken")
    public Boolean sendResetEmailToken (@Param("username") String username, @Param("email") String email){
        return userService.sendResetEmailToken(username,email);
    }

    @GetMapping("EmailExist")
    public ResponseEntity<Boolean> CheckIfEmailExist (@Param(("email")) String email){
        boolean msg =  userService.isExist(email);
        return new ResponseEntity<>(msg,HttpStatus.OK);
    }

}