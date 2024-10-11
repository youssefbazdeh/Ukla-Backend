package spark.ukla.creator_recipe;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.entities.Video;
import spark.ukla.repositories.VideoRepository;
import spark.ukla.services.implementations.UserService;

import java.util.List;

@RestController
@RequestMapping("/CreatorRecipe")
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatorRecipeController {

    private final UserService userService;
    private final CreatorRecipeRepository creatorRecipeRepository;
    private final VideoRepository videoRepository;
    private final CreatorRecipeService creatorRecipeService;

    public CreatorRecipeController(UserService userService, CreatorRecipeRepository creatorRecipeRepository, VideoRepository videoRepository, CreatorRecipeService creatorRecipeService) {
        this.userService = userService;
        this.creatorRecipeRepository = creatorRecipeRepository;
        this.videoRepository = videoRepository;
        this.creatorRecipeService = creatorRecipeService;
    }

  @PostMapping("/add")
    public ResponseEntity<?> add(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("videoId") Long videoId,
            @RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);
        if(creatorRecipeRepository.existsByTitle(title)){
            return new ResponseEntity<>("recipe name exists", HttpStatus.NOT_ACCEPTABLE);
        }
        Video video1 = videoRepository.getVideoById(videoId);
        if(video1==null){
            return new ResponseEntity<>("video doesn't exist",HttpStatus.NOT_ACCEPTABLE);
        }
        CreatorRecipe created = creatorRecipeService.add(title,video1,username,description);
        if(created!=null){
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        }
        else
            return new ResponseEntity<>(false, HttpStatus.NOT_ACCEPTABLE);

    }

    @GetMapping("/getAllByCreatorUsername")
    public ResponseEntity<?> getAllByCreatorUsername(@RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);
        List<CreatorRecipe> creatorRecipeList = creatorRecipeService.getAllByCreatorUsername(username);
        if(creatorRecipeList.isEmpty()){
            return  new ResponseEntity<>("no recipe found",HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(creatorRecipeList,HttpStatus.OK);
    }
    @GetMapping("/getall/{pageNo}/{pageSize}")
    public ResponseEntity<?> getAll(@RequestHeader("AUTHORIZATION") String header,@PathVariable int pageNo, @PathVariable int pageSize){
        List<CreatorRecipe> creatorRecipeList = creatorRecipeService.getAll(pageNo,pageSize);
        if(creatorRecipeList.isEmpty()){
            return  new ResponseEntity<>("no recipe found",HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(creatorRecipeList,HttpStatus.OK);
    }
    @GetMapping("/getbyid/{id}")
    public ResponseEntity<?> getbyid(@PathVariable("id")Long id){
        CreatorRecipe creatorRecipe = creatorRecipeService.getbyid(id);
        if(creatorRecipe!=null){
            return new ResponseEntity<>(creatorRecipe,HttpStatus.OK);
        }
        return new ResponseEntity<>("recipe not found",HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id")Long id){
        int deleted = creatorRecipeService.deleteCreatorRecipeById(id);
        if(deleted == 1){
            return new ResponseEntity<>("deleted",HttpStatus.OK);
        }
        return new ResponseEntity<>("failed",HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateById(
            @PathVariable("id") Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("videoId") Long videoId){
        Video video1 = videoRepository.getVideoById(videoId);
        if(video1==null){
            return new ResponseEntity<>("video doesn't exist",HttpStatus.NOT_ACCEPTABLE);
        }
        CreatorRecipe updated = creatorRecipeService.updateCreatorRecipe(id,title,video1,description);
        if(updated!=null){
            return new ResponseEntity<>(updated,HttpStatus.OK);
        }
        return new ResponseEntity<>(false,HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/updateStatus/{id}")
    public ResponseEntity<?> updatestatus(@PathVariable("id")Long id,@RequestParam("status") String status){
        if( creatorRecipeService.updateRecipeStatus(id,status) ){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(false,HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
