package spark.ukla.utils;

import lombok.*;
import spark.ukla.entities.enums.Gender;

import java.util.Date;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SignInObject {


        private String idtoken;
        private String username;
        private Gender gender;
        private Date birthdate;



}
