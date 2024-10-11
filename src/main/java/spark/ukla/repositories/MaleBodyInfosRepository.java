package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.User;
import spark.ukla.entities.bodyinfos.MaleBodyInfo;
@Repository
public interface MaleBodyInfosRepository extends JpaRepository<MaleBodyInfo, Long> {
    boolean existsByUser(User user);

    MaleBodyInfo findByUser(User user);

}
