package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {
}
