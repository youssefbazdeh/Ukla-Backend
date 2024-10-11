package spark.ukla.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.DTO.PlanDTO;
import spark.ukla.entities.PlanOfWeek;
import spark.ukla.repositories.projection.PlanOfWeekProjection;
import spark.ukla.services.implementations.PlanOfWeekService;
import spark.ukla.services.implementations.UserService;
import spark.ukla.services.interfaces.IPlanOfWeek;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/Plan")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlanOfWeekController {

    @Autowired
    IPlanOfWeek iPlanOfWeek;
    private final UserService userService;
    private final PlanOfWeekService planOfWeekService;

    public PlanOfWeekController(UserService userService, PlanOfWeekService planOfWeekService) {
        this.userService = userService;
        this.planOfWeekService = planOfWeekService;
    }

    @PostMapping("/addPLan")
    public PlanOfWeek addPlan(@RequestHeader("AUTHORIZATION") String header,@RequestBody LocalDate userDate){
        String username = userService.getusernamefromtoken(header);
        return iPlanOfWeek.addPlan(userDate,username);
    }

    @PutMapping("/update")
    String update(@RequestBody PlanOfWeek planOfWeek){
        return iPlanOfWeek.update(planOfWeek);
    }

    @GetMapping("/retrieveAllByUser/{page}/{size}")
    List<PlanOfWeekProjection> retrieveAllByUser(@RequestHeader("AUTHORIZATION") String header, @PathVariable int page, @PathVariable int size){
      String username = userService.getusernamefromtoken(header);
        Pageable pageable;
        if (page == 0 && size == 0) {
            // Return the full list
            pageable = Pageable.unpaged();
        } else {
            // Return paginated results
            pageable = PageRequest.of(page-1, size);
        }
        return iPlanOfWeek.retrieveAllBYUser(username,pageable);
    }

    @GetMapping("/retrieveByName/{name}")
    PlanOfWeek retrieveByName(@PathVariable String name){
        return iPlanOfWeek.retrieveByName(name);
    }

    @GetMapping("/retrieveById/{idPlan}")
    PlanOfWeek retrieveById(@PathVariable Long idPlan){
        return iPlanOfWeek.retrieveById(idPlan);
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<?> deletePlan(@PathVariable Long id){
        try {
            boolean isDeleted = iPlanOfWeek.deleteById(id);
            if (isDeleted) {
                return new ResponseEntity<>("Plan deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Plan not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting plan", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    @PostMapping("/follow-plan/{planId}")
    public ResponseEntity<String> followPlan(@RequestHeader("AUTHORIZATION") String header,@PathVariable Long planId){
        String username = userService.getusernamefromtoken(header);

        if(!iPlanOfWeek.followPlan(planId,username)) {
            return new ResponseEntity<>("plan not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("plan followed", HttpStatus.OK);
    }


    @PutMapping("/renamePlan/{newPlanName}/{id}")
    public String renamePlan( @PathVariable String newPlanName ,@PathVariable long id ) {
        return iPlanOfWeek.renamePlan(newPlanName,id) ;
    }
    @GetMapping("/getFollowedPlan")
    public ResponseEntity<?> getFollowedPlan(@RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);
        List<PlanDTO> followedPlansList = planOfWeekService.getFollowedPlan(username);
        if(followedPlansList.isEmpty()){
            return new ResponseEntity<>("user has no followed plan",HttpStatus.NO_CONTENT);
        }else {
            return new ResponseEntity<>(followedPlansList,HttpStatus.OK);
        }
    }
    @PutMapping("/changeTheDateOfThePlan/{planId}")
    public ResponseEntity changeTheDateOfThePlan(@PathVariable Long planId,@RequestBody LocalDate date){
        planOfWeekService.changeDateOfPlan(planId,date);
        return new ResponseEntity<>("Date changed", HttpStatus.OK);
    }

}