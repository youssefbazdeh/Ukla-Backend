package spark.ukla.services.interfaces;

import spark.ukla.entities.User;

public interface IForgetPasswordService {
	Boolean updateResetPasswordToken(String email);

	User retrieveByResetPasswordToken(String resetPasswordToken);

	String updatePassword(User user, String newPassword);

	String updatePassword1(String code, String newPassword);
}
