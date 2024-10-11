package spark.ukla.creator_feature;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spark.ukla.entities.Image;
import spark.ukla.services.implementations.FileLocationService;
import spark.ukla.services.implementations.RegistrationService;
import spark.ukla.services.implementations.UserService;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/creator")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreatorController {

    private final RegistrationService registrationService;
    private final CreatorService creatorService;
    private final UserService userService;
    private final FileLocationService fileLocationService;

    public CreatorController(RegistrationService registrationService, CreatorService creatorService, FileLocationService fileLocationService, UserService userService) {
        this.registrationService = registrationService;
        this.creatorService = creatorService;
        this.fileLocationService = fileLocationService;
        this.userService = userService;
    }


    @PostMapping(value = "/add")
	public ResponseEntity<String> add(@Valid @RequestPart("creator") Creator creator, @RequestPart("image") MultipartFile image) throws Exception{
        Image savedImage = fileLocationService.save(image);
        if (savedImage==null){
            return new ResponseEntity<>("image not saved", HttpStatus.NOT_MODIFIED);
        }
        String msg = registrationService.registerCreator(creator,savedImage);
        if (Objects.equals(msg, "username exists"))
            return new ResponseEntity<>(msg, HttpStatus.FOUND);

        if (Objects.equals(msg, "creator saved"))
            return new ResponseEntity<>(msg, HttpStatus.CREATED);
        else
            return new ResponseEntity<>(msg ,HttpStatus.NOT_ACCEPTABLE);
	}

    @PutMapping("update")
    public ResponseEntity<String> updateProfile(@RequestPart Creator creator, @RequestHeader("AUTHORIZATION") String header) {
        String username = userService.getusernamefromtoken(header);

        return creatorService.updateCreatorProfile(creator,username);
    }
    @PutMapping("/updateImage")
    public ResponseEntity<String> updateImage(@RequestParam MultipartFile image, @RequestHeader("AUTHORIZATION") String header) throws Exception {
        String username = userService.getusernamefromtoken(header);

        Image image1 = fileLocationService.save(image);
        if (image1 == null) {
            return new ResponseEntity<>("image not saved", HttpStatus.NOT_ACCEPTABLE);
        }
        if(creatorService.updateImage(image1, username)){
            return new ResponseEntity<>("image updated", HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("creator not found", HttpStatus.NOT_FOUND);

    }

    @GetMapping("/getByUsername")
    public ResponseEntity<?> retrieveByUsername( @RequestHeader("AUTHORIZATION") String header){
        String username = userService.getusernamefromtoken(header);

        Optional<Creator> creator = registrationService.retrieveByUsername(username);
        if(creator.isPresent())
            return new ResponseEntity<>(creator, HttpStatus.OK);
        else
            return new ResponseEntity<>("creator does not exist", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/followCreator/{idCreator}")
    public ResponseEntity<?> followCreator(@RequestHeader("AUTHORIZATION") String header,
                                           @PathVariable Long idCreator){
        String username = userService.getusernamefromtoken(header);

        creatorService.followCreator(username,idCreator);
        return new ResponseEntity<>("creator followed", HttpStatus.OK);

    }

    @PutMapping("/unfollowCreator/{idCreator}")
    public ResponseEntity<?> unfollowCreator(@RequestHeader("AUTHORIZATION") String header,
                                           @PathVariable Long idCreator) {
        String username = userService.getusernamefromtoken(header);

        creatorService.unfollowCreator(username, idCreator);
        return new ResponseEntity<>("creator unfollowed", HttpStatus.OK);

    }

    @GetMapping("/getById/{creatorId}")
    public ResponseEntity<?> getCreatorById(@RequestHeader("AUTHORIZATION") String header,@PathVariable("creatorId")Long creatorId){
        String username = userService.getusernamefromtoken(header);
        CreatorDTOForProfile creatorDTOForProfile = creatorService.getCreatorByIdForProfile(creatorId,username);
        return new ResponseEntity<>(creatorDTOForProfile,HttpStatus.OK);
    }
}
