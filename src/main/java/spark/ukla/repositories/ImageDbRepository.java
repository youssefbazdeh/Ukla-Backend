package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Image;
@Repository
public interface ImageDbRepository extends JpaRepository<Image, Long> {
}