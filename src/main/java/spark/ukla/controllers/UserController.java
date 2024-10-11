package spark.ukla.controllers;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spark.ukla.DTO.UserDTO;
import spark.ukla.entities.Profile;
import spark.ukla.entities.User;
import spark.ukla.repositories.ProfileRepository;
import spark.ukla.services.implementations.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {


	private final UserService userService;
	private final ProfileRepository profileRepository;

	public UserController(UserService userService, ProfileRepository profileRepository) {
		this.userService = userService;
        this.profileRepository = profileRepository;
    }


	@PutMapping("/updatePassword")
	public ResponseEntity<String> updatePassword(@RequestHeader("AUTHORIZATION") String header, @RequestBody String password) {
		String username = userService.getusernamefromtoken(header);

		if (username!=""){
			String msg = userService.updatePassword(username,password) ;
			return new ResponseEntity<>(msg, HttpStatus.OK);
		}


		else
			return new ResponseEntity<>("error", HttpStatus.NOT_MODIFIED);

	}




	@GetMapping("/checkIfPasswordIsCorrect")
	public ResponseEntity<Boolean> checkIfPasswordIsCorrect(@Param("username") String username, @Param("password") String password) {

		boolean msg = userService.checkIfPasswordIsCorrect(username, password);
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}



	@PostMapping("/addAdmin")
	public ResponseEntity<UserDTO> add(@Valid @RequestBody UserDTO adminDTO) {
		UserDTO userSaved = userService.addAdmin(adminDTO);
		if (userSaved != null)
			return new ResponseEntity<>(userSaved, HttpStatus.CREATED);
		else
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

	}
	// Add Role Methode
	@PostMapping("/setRole/{id}/{role}")
	public ResponseEntity<?> setUser(@PathVariable("id") long id,@PathVariable("role") String role) {

		userService.setRoleUser(id, role);
		return new ResponseEntity<>("saved",HttpStatus.OK);
	}
	@PutMapping("/update")
	public ResponseEntity<UserDTO> update(@RequestBody UserDTO userDTO) {

		UserDTO userUpdated = userService.update(userDTO);
		if (userUpdated != null)
			return new ResponseEntity<>(userUpdated, HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

	}

	@PutMapping("/updateUsername")
	public ResponseEntity<UserDTO> updateUsername(@RequestBody UserDTO userDTO) {
		UserDTO userUpdated = userService.updateUsername(userDTO);
		if (userUpdated != null)
			return new ResponseEntity<>(userUpdated, HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

	}

	@PutMapping("/updateEmail")
	public ResponseEntity<UserDTO> updateEmail(@RequestBody UserDTO userDTO) {
		UserDTO userUpdated = userService.updateEmail(userDTO);
		if (userUpdated != null)
			return new ResponseEntity<>(userUpdated, HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

	}
	@PutMapping("/updateBithdate")
	public ResponseEntity<UserDTO> updateBirthdate(@RequestBody UserDTO userDTO) {
		UserDTO userUpdated = userService.updateBirthDate(userDTO);
		if (userUpdated != null)
			return new ResponseEntity<>(userUpdated, HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);

	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> delete(@PathVariable("id") long id) {
		userService.delete(id);
		return new ResponseEntity<>("user deleted",HttpStatus.OK);
	}

	@PostMapping("/retrieveByUsername")
	public ResponseEntity<UserDTO> retrieveByUsername(@RequestBody UserDTO userDTO) {
		UserDTO userRetreived = userService.retrieveByUsername(userDTO.getUsername());
		if (userRetreived != null)
			return new ResponseEntity<>(userRetreived, HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

	}

	@GetMapping("/retrieveByEmail")
	public ResponseEntity<UserDTO> retrieveByEmail(@Param("email") String email) {
		UserDTO userRetreived = userService.retrieveByEmail(email);
		if (userRetreived != null)
			return new ResponseEntity<>(userRetreived, HttpStatus.OK);
		else
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/retrieveAll")
	public ResponseEntity<List<UserDTO>> retrieveAll() {
		List<UserDTO> usersToReturn = userService.retrieveAll();
		return new ResponseEntity<>(usersToReturn, HttpStatus.OK);
	}

	@PutMapping("/lockUser/{id}")
	public ResponseEntity<String> lockUser(@PathVariable("id") long id) {
		String msgToReturn = userService.lockUser(id);
		return new ResponseEntity<>(msgToReturn, HttpStatus.OK);
	}


	@PutMapping("/unlockUser/{id}")
	public ResponseEntity<String> unlockUser(@PathVariable("id") long id) {
		String msgToReturn = userService.unlockUser(id);
		return new ResponseEntity<>(msgToReturn, HttpStatus.OK);
	}

	@PutMapping("/updateEmail1")
	public ResponseEntity<String> updateEmail1(@RequestBody Parametres parametres) {
		String msg = userService.updateEmail1(parametres.code, parametres.email);
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}

	private static class Parametres {
		String code;
		String email;

		public Parametres(String code, String email) {
			this.code = code;
			this.email = email;
		}
	}
	@PostMapping("/checkIfPasswordIsCorrect")
	public ResponseEntity<String> checkIfPasswordAccountIsCorrect(@RequestHeader("AUTHORIZATION") String header, @Param("password") String password){

		String msg = "";
		String username = userService.getusernamefromtoken(header);
		if(userService.checkIfPasswordIsCorrect(username, password)){
			msg = "Correct Password";
		}else{
			msg = "wrong password";
		}
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}


	@DeleteMapping("/deleteAccountByUsername")
	public ResponseEntity<String> deleteAccountByUsername(@RequestHeader("AUTHORIZATION") String header) {
		String username = userService.getusernamefromtoken(header);
		boolean userDeleted= userService.deleteAccountByUsername(username);
		if (userDeleted){
			String message = "User '" + username + "' deleted successfully";

			return ResponseEntity.ok(message);
		}
		else {
			String message = "User '" + username + "' not deleted ";
			return ResponseEntity.status(HttpStatus.NON_AUTHORITATIVE_INFORMATION).body(message);
		}
	}

	@PutMapping("/setOnBoardingScreenToFalse")
	public ResponseEntity<?> setOnBoardingScreenToFalse(@RequestHeader("AUTHORIZATION")String header){
		String username = userService.getusernamefromtoken(header);
		User user = userService.findByUsername(username);
		Profile profile = user.getProfile();
		profile.setOnBoardingScreen(false);
		profileRepository.save(profile);
		return new ResponseEntity<>(false,HttpStatus.OK);
	}

}

