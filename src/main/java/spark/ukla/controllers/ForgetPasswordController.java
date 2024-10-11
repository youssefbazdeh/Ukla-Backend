package spark.ukla.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import spark.ukla.DTO.UserDTO;
import spark.ukla.converters.UserConverter;
import spark.ukla.services.implementations.ForgetPasswordService;

@RestController  @Transactional
@RequestMapping("/forgetPassword")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgetPasswordController {
	@Autowired
	ForgetPasswordService forgetPasswordService;
	@Autowired
	UserConverter userConverter;

	@PostMapping("/forgot_password")

	public ResponseEntity<String> processForgotPassword(@RequestBody UserDTO userDTO) {
		spark.ukla.entities.User user = userConverter.dtoToEntity(userDTO);
		Boolean test = forgetPasswordService.updateResetPasswordToken(user.getEmail());
		if (test) {
			return new ResponseEntity<>("password token saved",HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} 
	}

	@PostMapping("/reset_password")
	public ResponseEntity<String> showResetPasswordForm(@Param(value = "token") String token) {
		String msg;
		spark.ukla.entities.User user = forgetPasswordService.retrieveByResetPasswordToken(token);
		if (user == null){
			msg = "invalid Token";
			return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
		}
		else{
			msg = "interface change your password";
			return new ResponseEntity<>(msg, HttpStatus.OK);
		}
	}

	@PutMapping("/updatePassword")
	public ResponseEntity<String> updatePassword(@RequestBody spark.ukla.entities.User user) {
		String msg = forgetPasswordService.updatePassword(user, user.getPassword());
		return new ResponseEntity<>(msg, HttpStatus.OK);

	}

	@PutMapping("/updatePassword1")
	public ResponseEntity<String> updatePassword1 (@RequestBody Parametres parametres ){
		String msg = forgetPasswordService.updatePassword1(parametres.code, parametres.password);
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}
	@GetMapping("/getByToken/{token}")
	public ResponseEntity<UserDTO> getByToken (@PathVariable String token ){

		return new ResponseEntity(forgetPasswordService.retrieveByResetPasswordToken(token), HttpStatus.OK);

	}


	private static class Parametres{
		String code;
		String password;

		public Parametres(String code, String password){
			this.code = code;
			this.password = password;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}