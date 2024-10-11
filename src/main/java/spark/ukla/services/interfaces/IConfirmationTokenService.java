package spark.ukla.services.interfaces;

import java.util.Optional;

import spark.ukla.entities.ConfirmationToken;
import spark.ukla.entities.User;

public interface IConfirmationTokenService {
	ConfirmationToken add(ConfirmationToken confirmationToken);

	Optional<ConfirmationToken> getByToken(String token);

	int updateConfirmedAt(String token);

	void deleteToken(User user);

}