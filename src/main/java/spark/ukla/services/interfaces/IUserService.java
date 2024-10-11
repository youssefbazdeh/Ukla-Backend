package spark.ukla.services.interfaces;

import spark.ukla.DTO.UserDTO;

import java.util.List;

public interface IUserService {

	UserDTO addAdmin(UserDTO userDTO);

	UserDTO update(UserDTO userDTO);
	
	UserDTO updateUsername(UserDTO userDTO);

	UserDTO updateEmail(UserDTO userDTO);

	UserDTO updateBirthDate(UserDTO userDTO);

	void delete(long id);

	UserDTO retrieveByUsername(String username);

	UserDTO retrieveByEmail(String email);

	spark.ukla.entities.User findUserByEmail(String email);

	List<UserDTO> retrieveAll();

	spark.ukla.entities.User retrieveByResetEmailToken(String resetPasswordToken);

	String updateEmail1(String code, String email);

	String sendCodeForUpdateEmail(String username, String email);

	String updateNewEmail(String token, String email);

	Boolean sendResetEmailToken(String username, String email);

	boolean checkIfPasswordIsCorrect(String username, String password);



	boolean isExist (String email);



	String updatePassword(String username, String password) ;

	boolean deleteAccountByUsername(String username);

	boolean checkUsernameExists(String username);


}
