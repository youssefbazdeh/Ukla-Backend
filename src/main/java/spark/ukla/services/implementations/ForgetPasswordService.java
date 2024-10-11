package spark.ukla.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import spark.ukla.entities.User;
import spark.ukla.repositories.UserRepository;
import spark.ukla.services.interfaces.IForgetPasswordService;
import spark.ukla.utils.EmailService;
import spark.ukla.utils.RandomUtil;

@Slf4j
@Service
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgetPasswordService implements IForgetPasswordService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	EmailService emailService;

	@Override
	public Boolean updateResetPasswordToken(String email) {
		User user = userRepository.findByEmail(email);
		if (user != null) {
			String token = RandomUtil.generateRandomStringNumber(6).toUpperCase();
			user.setResetPasswordToken(token);
			userRepository.save(user);
			log.info("password token saved.");

			emailService.send(email, "Password Reset Request",
					emailService.buildForgetPasswordEmail(user.getUsername(), "We received a request to reset your Ukla password. Enter the following code:", token));




			return true;

		} else {
			log.error("user not found.");
			return false;
		}
	}

	@Override
	public User retrieveByResetPasswordToken(String resetPasswordToken) {
		return userRepository.findByResetPasswordToken(resetPasswordToken);
	}

	@Override
	public String updatePassword(User user, String newPassword) {
		User UserExist = userRepository.findByEmail(user.getEmail());
		UserExist.setPassword(bCryptPasswordEncoder.encode(newPassword));
		UserExist.setResetPasswordToken(null);
		userRepository.save(UserExist);
		return "password updated";
	}

    @Override
    public String updatePassword1(String resetPasswordToken, String newPassword) {
		User userExist = userRepository.findByResetPasswordToken(resetPasswordToken);
		if(userExist == null){
			return "invalid token";
		}
		userExist.setPassword(bCryptPasswordEncoder.encode(newPassword));
		userExist.setResetPasswordToken(null);
		userRepository.save(userExist);
		return "password updated";
    }
}