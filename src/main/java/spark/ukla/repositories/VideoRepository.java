package spark.ukla.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spark.ukla.entities.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Video findVideoBySasUrl(String url);
    Video getVideoById(Long id);
}