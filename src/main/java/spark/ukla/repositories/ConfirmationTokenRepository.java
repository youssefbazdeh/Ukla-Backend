package spark.ukla.repositories;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import spark.ukla.entities.ConfirmationToken;
import spark.ukla.entities.User;

@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
	Optional<ConfirmationToken> findByToken(String token);
	
	ConfirmationToken findByUser(User user);

	@Modifying
	@Query("update ConfirmationToken c set c.confirmedAt = :confirmedAt where c.token = :token")
	int updateConfirmedAt(@Param("token") String token, @Param("confirmedAt") LocalDateTime confirmedAt);
}

