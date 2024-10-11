package spark.ukla;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching

public class UklaApplication {

	public static void main(String[] args) {
		SpringApplication.run(UklaApplication.class, args);

	}
}
