package spark.ukla.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import spark.ukla.entities.PlanOfWeek;
import spark.ukla.entities.Profile;
import spark.ukla.entities.Recipe;
import spark.ukla.entities.enums.Gender;
import spark.ukla.entities.enums.Role;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDTO {
	Long id;
	@NotBlank(message = "Firstname required")
	String firstName;
	@NotBlank(message = "Lastname required")
	String lastName;
	@Past
	Date birthdate;
	@Enumerated(EnumType.STRING)
	Gender gender ;
	@NotBlank(message = "Username required")
	String username;
	@NotBlank(message = "Email required")
	@Email
	String email;
	@NotBlank(message = "Password required")
	@Size(min = 8, max = 60, message = "password must have 8 to 20 caracters.")
	String password;
	Role role;
	Boolean locked = false;
	Boolean enabled = false;
	Profile profile;

	@JsonIgnore
	Set<Recipe> favoris;
	@JsonIgnore
	Set<PlanOfWeek> plansOfWeek;

	public boolean isAccountNonLocked() {
		return !locked;
	}

	public boolean isEnabled() {
		return enabled;
	}
}
