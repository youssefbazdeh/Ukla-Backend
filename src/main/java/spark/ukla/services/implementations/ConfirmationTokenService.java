package spark.ukla.services.implementations;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.ConfirmationToken;
import spark.ukla.entities.User;
import spark.ukla.repositories.ConfirmationTokenRepository;
import spark.ukla.services.interfaces.IConfirmationTokenService;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmationTokenService implements IConfirmationTokenService {
	@Autowired
	ConfirmationTokenRepository confirmationTokenRepository;
	
	
	
	@Override
	public ConfirmationToken add(ConfirmationToken confirmationToken) {
		return confirmationTokenRepository.save(confirmationToken);
	}

	@Override
	public int updateConfirmedAt(String token) {
		return confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
	}

	@Override
	public Optional<ConfirmationToken> getByToken(String token) {
		return confirmationTokenRepository.findByToken(token);
	}
	

	@Override
	public void deleteToken(User user){
		ConfirmationToken ct = confirmationTokenRepository.findByUser(user);
		confirmationTokenRepository.delete(ct);
	}

	

}
