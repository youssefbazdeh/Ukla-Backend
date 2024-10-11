package spark.ukla.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import spark.ukla.entities.Video;

import java.io.IOException;
import java.util.List;

public interface IVideoService {

    Video getVideo(String name);

    String saveVideo(MultipartFile file, String name) throws IOException;

    List<String> getAllVideoNames();

    List<Video> findAll();
}
