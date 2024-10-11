package spark.ukla.services.implementations;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spark.ukla.DTO.UserDTO;
import spark.ukla.creator_feature.CreatorConverter;
import spark.ukla.converters.UserConverter;
import spark.ukla.entities.ConfirmationToken;
import spark.ukla.creator_feature.Creator;
import spark.ukla.entities.User;
import spark.ukla.entities.enums.AuthentificationProvider;
import spark.ukla.entities.enums.Role;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.interfaces.IUserService;
import spark.ukla.utils.EmailService;
import spark.ukla.utils.RandomUtil;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;


@Slf4j
@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService implements IUserService, UserDetailsService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	ConfirmationTokenService confirmationTokenService;
  
	@Autowired
	EmailService emailService;
	@Autowired
	UserConverter userConverter;
	@Autowired
	CreatorConverter creatorConverter;

	@Override
	public String updatePassword(String username, String newPassword) {
		User UserExist = userRepository.findByUsername(username);
		if(UserExist!= null){
			UserExist.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(UserExist);
			return "password updated";}
		else return "user does not exist" ;
	}


	public spark.ukla.entities.User findByUsername(String username) {
        return userRepository.findByUsername(username);
	}

	public Creator findByUsernameCreator(String username){
		User user = userRepository.findByUsername(username);
		return (Creator) user;
	}
	@Override
	public UserDTO addAdmin(UserDTO userDTO) {
		User user = userConverter.dtoToEntity(userDTO);
		Boolean ExistsByUsername = userRepository.existsByUsername(user.getUsername());
		Boolean ExistsByEmail = userRepository.existsByEmail(user.getEmail());
		if (ExistsByUsername) {
			log.error("Username exists.");
			return null;
		} else if (ExistsByEmail) {
			log.error("Email exists.");
			return null;
		} else {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setRole(Role.ADMIN);
			log.info("saving user {} to the database",user.getUsername());
			userRepository.save(user);
			User userSaved = userRepository.save(user);

			String token = RandomUtil.generateRandomStringNumber(6).toUpperCase();
			ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
					LocalDateTime.now().plusMinutes(5), user);
			confirmationTokenService.add(confirmationToken);

			emailService.send(user.getEmail(), "Confirm your email.", emailService.buildEmail(user.getUsername(),
					"Thank you for registering. Please activate your account:", token));
			log.info("Admin saved, confirm your email.");

			return userConverter.entityToDTO(userSaved);
		}
	}
	public void  setRoleUser(long id,String role ) {

		Role newRole=Role.USER;
		for (Role value : Role.values()) {
			if (Objects.equals(role, value.toString())){
				newRole=value;
				break;
			}

		}
		User user = userRepository.findById(id).get();
		user.setRole(newRole);

		userRepository.save(user);

	}
	@Override
	public UserDTO update(UserDTO userDTO) {
		User user = userConverter.dtoToEntity(userDTO);
		Boolean Exists = userRepository.existsById(user.getId());
		if (!Exists) {
			log.error("User not found.");
			return null;
		} else {
			userRepository.update(user.getFirstName(), user.getLastName(), user.getBirthdate(),
					user.getId());
				log.info("User updated.");
				return userDTO;
		}
	}

	@Override
	public UserDTO updateUsername(UserDTO userDTO) {
		User user = userConverter.dtoToEntity(userDTO);
		String username = userRepository.findById(user.getId()).get().getUsername();

		if (user.getUsername().equals(username)) {
			log.error("You have already this username.");
			return null;
		} else if (userRepository.existsByUsername(user.getUsername())) {
			log.error("This username is already taken.");
			return null;
		} else {
			userRepository.updateUsername(user.getUsername(), user.getId());
			log.info("User updated");
			return userDTO;
		}
	}

	@Override
	public UserDTO updateEmail(UserDTO userDTO) {
		User user = userConverter.dtoToEntity(userDTO);
		String email = userRepository.findById(user.getId()).get().getEmail();
		if (user.getEmail().equals(email)) {
			log.error("You have already this email.");
			return null;
		} else if (userRepository.existsByUsername(user.getUsername())) {
			log.error("This email is already taken.");
			return null;
		} else {
			userRepository.updateEmail(user.getEmail(), user.getId());
			log.info("User updated");
			return userDTO;
		}
	}

	@Override
	public UserDTO updateBirthDate(UserDTO userDTO) {
		User user = userConverter.dtoToEntity(userDTO);
		Date birthdate = userRepository.findById(user.getId()).get().getBirthdate();
		if (user.getBirthdate().equals(birthdate)) {
			log.error("You have already this birthdate.");
			return null;
		}else {
			userRepository.updateBirthdate(user.getBirthdate(), user.getId());
			log.info("User updated");
			return userDTO;
		}
	}


	@Override
	public void delete(long id) {
		User exists = userRepository.findById(id).get();
        confirmationTokenService.deleteToken(exists);
        userRepository.delete(exists);
        log.error("User deleted.");
    }

	@Override
	public UserDTO retrieveByUsername(String username) {
		User user = userRepository.findByUsername(username);
		if(user instanceof Creator){
			Creator creator = (Creator) user;
            return creatorConverter.entityToDTO(creator);
		}
		if (user != null)
			return userConverter.entityToDTO(user);
		else
			return null;
	}

	@Override
	public UserDTO retrieveByEmail(String email) {
		User user = userRepository.findByEmail(email);
		if(user instanceof Creator){
			Creator creator = (Creator) user;
			return creatorConverter.entityToDTO(creator);
		}
		if (user != null)
			return userConverter.entityToDTO(user);
		else
			return null;
	}

	@Override
	public spark.ukla.entities.User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<UserDTO> retrieveAll() {
		List<spark.ukla.entities.User> users = (List<spark.ukla.entities.User>) userRepository.findAll();
		return userConverter.entitiesToDTOS(users);
	}
	@Override
	public spark.ukla.entities.User retrieveByResetEmailToken(String resetEmailToken) {
		return userRepository.findByResetPasswordToken(resetEmailToken);
	}

	@Override
	public String updateEmail1(String resetPasswordToken, String email) {
		spark.ukla.entities.User userExist = userRepository.findByResetPasswordToken(resetPasswordToken);
		if(userExist == null){
			return "invalid token";
		}
		userExist.setEmail(email);
		userExist.setResetPasswordToken(null);
		userRepository.save(userExist);
		return "email updated";
	}




	@Override
	public String updateNewEmail(String token, String email) {
		spark.ukla.entities.User user = userRepository.findByCheckForPassword(token);
		boolean test = userRepository.existsByEmail(email);
		if (user == null) {
			return "invalid code";
		}else if (test) {
			return "email exist";
		} else {
			user.setEmail(email);
			user.setCheckForPassword(null);
			userRepository.save(user);
			return "email updated ";
		}
	}

	@Override
	public Boolean sendResetEmailToken(String username, String email) {
		spark.ukla.entities.User user = userRepository.findByUsername(username);
		if (user != null) {
			String token = RandomUtil.generateRandomStringNumber(6).toUpperCase();
			user.setCheckForPassword(token);
			userRepository.save(user);
			log.info("email token saved.");
			emailService.send(email, "Update Email",
					emailService.buildEmail(email, "Enter the code below to update your email", token));
			return true;
		}
		return false;
	}


	@Override
	public String sendCodeForUpdateEmail(String username, String email) {
		String msg = "";
		String token = RandomUtil.generateRandomStringNumber(6).toUpperCase();
		spark.ukla.entities.User user1 = userRepository.findByUsername(username);
		user1.setCheckForPassword(token);
		System.out.println(token);
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setText("please confirm the code below to change your email");
			mailMessage.setTo(email);
			mailMessage.setSubject(token);
			userRepository.save(user1);
			msg = "email sent";

		} catch (Exception e) {
			msg = "Error occured: " + e.getMessage();
			System.out.println("Error occured: " + e.getMessage());
		}
		return msg;
	}

	@Override
	public boolean checkIfPasswordIsCorrect(String username, String password) {

		System.out.println(username);
		spark.ukla.entities.User user = userRepository.findByUsername(username);
		return bCryptPasswordEncoder.matches(password, user.getPassword());
	}

	@Override
	public boolean isExist(String email) {
		boolean status ;
		Optional<spark.ukla.entities.User> user = Optional.ofNullable(userRepository.findByEmail(email));
		status = !user.isPresent();
		return !status;
	}
	public String lockUser(long id) {
		userRepository.lockUser(id);

		return "User locked";
	}

	public String unlockUser(long id) {
		userRepository.unlockUser(id);
		return "User unlocked";
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		spark.ukla.entities.User user = userRepository.findByUsername(username);
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(user.getRole().getAuthority()));
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);
	}


	public String getusernamefromtoken(String header)
	{
		String username="" ;
		String authorizationHeader = header ;
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				String token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(token);
				username = decodedJWT.getSubject();

				String[] roles = decodedJWT.getClaim("role").asArray(String.class); // to remove if it doesn't break anything

			}
			catch (Exception exception) {
				log.error("Error getting username in : {}", exception.getMessage());

			}

		}

		return (username);
	}


	public void processOAuthPostLogin(String email) {
		spark.ukla.entities.User existUser = userRepository.findByUsername(email) ;

		if (existUser == null) {
			spark.ukla.entities.User newUser = new spark.ukla.entities.User();
			newUser.setEmail(email);
			newUser.setProvider(AuthentificationProvider.USERNAME_PASSWORD);
			newUser.setEnabled(true);
			userRepository.save(newUser);
		}

	}


	@Override
	public boolean deleteAccountByUsername(String username) {
		boolean userDeleted;
		User user = userRepository.findByUsername(username);

		if (user != null) {
			confirmationTokenService.deleteToken(user);
			userRepository.deleteByUsername(user.getUsername());
			return userDeleted =true;
		} else
			log.error("User not found.");
		    return userDeleted =false;

	}

	@Override
	public boolean checkUsernameExists(String username) {
		return userRepository.existsByUsername(username) ;
	}


}