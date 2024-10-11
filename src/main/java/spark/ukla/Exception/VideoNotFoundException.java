package spark.ukla.Exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "A video not found with this name ")
public class VideoNotFoundException extends RuntimeException{
}
