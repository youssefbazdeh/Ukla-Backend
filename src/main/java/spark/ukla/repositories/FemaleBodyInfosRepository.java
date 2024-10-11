package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.User;
import spark.ukla.entities.bodyinfos.FemaleBodyInfo;

@Repository
public interface FemaleBodyInfosRepository extends JpaRepository<FemaleBodyInfo,Long> {
    boolean existsByUser(User user);

    FemaleBodyInfo findByUser(User user);
}
