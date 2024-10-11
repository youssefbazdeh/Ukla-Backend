package spark.ukla.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.entities.User;
import spark.ukla.entities.bodyinfos.BodyInfo;
import spark.ukla.entities.bodyinfos.FemaleBodyInfo;
import spark.ukla.entities.bodyinfos.MaleBodyInfo;
import spark.ukla.entities.enums.Gender;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.implementations.NutritionService;
import spark.ukla.services.implementations.UserService;

import java.beans.FeatureDescriptor;

@RestController
@RequestMapping("/nutrition")
public class NutritionController {
    private final NutritionService nutritionService ;
    private final UserService userService;
    private final UserRepository userRepository ;


    public NutritionController(NutritionService nutritionService, UserService userService, UserRepository userRepository) {
        this.nutritionService = nutritionService;
        this.userService = userService;
        this.userRepository = userRepository ;
    }

    @PostMapping("/addmalebodyinfos")
    public ResponseEntity<MaleBodyInfo> addMaleBodyInfos(@RequestHeader("AUTHORIZATION") String header,@RequestBody MaleBodyInfo maleBodyInfos){
        String username = userService.getusernamefromtoken(header);
        MaleBodyInfo savedMaleBodyInfo = nutritionService.saveBodyInfosMale(maleBodyInfos,username);
        return  ResponseEntity.status(HttpStatus.OK).body(savedMaleBodyInfo) ;
    }
    @PostMapping("/addfemalebodyinfos")
    public ResponseEntity<FemaleBodyInfo> addFemaleBodyInfos(@RequestHeader("AUTHORIZATION") String header, @RequestBody FemaleBodyInfo femaleBodyInfo){
        String username = userService.getusernamefromtoken(header);
        FemaleBodyInfo savedFemaleBodyInfo = nutritionService.saveBodyInfosFemale(femaleBodyInfo,username);
        return  ResponseEntity.status(HttpStatus.OK).body(savedFemaleBodyInfo) ;
    }


    @GetMapping("/getBodyInfo")
    public ResponseEntity<?> getCalorieNeeds(@RequestHeader("AUTHORIZATION") String header) {
        String username = userService.getusernamefromtoken(header) ;
        User user = userRepository.findByUsername(username) ;

        if(user.getGender().equals(Gender.Female)) {
           FemaleBodyInfo femaleBodyInfo =  nutritionService.getFemaleBodyInfo(username) ;
             return  ResponseEntity.ok(femaleBodyInfo.getCalorieNeed()) ;

        }
        else {
           MaleBodyInfo maleBodyInfo =  nutritionService.getMaleBodyInfo(username) ;
             return  ResponseEntity.ok(maleBodyInfo.getCalorieNeed()) ;

        }

    }





}
