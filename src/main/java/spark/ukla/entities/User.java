package spark.ukla.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spark.ukla.creator_feature.Creator;
import spark.ukla.entities.bodyinfos.FemaleBodyInfo;
import spark.ukla.entities.bodyinfos.MaleBodyInfo;
import spark.ukla.entities.enums.AuthentificationProvider;
import spark.ukla.entities.enums.Gender;
import spark.ukla.entities.enums.Role;
import spark.ukla.entities.groceyList.GroceryList;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table( uniqueConstraints = { @UniqueConstraint(name = "User_email_unique", columnNames = "email"),
		@UniqueConstraint(name = "User_username_unique", columnNames = "username") })
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements Serializable, UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	Long id;
	@Column(name = "FIRST_NAME", nullable = false)
	@NotBlank(message = "Firstname required")
	String firstName;
	@Column(name = "LAST_NAME", nullable = false)
	@NotBlank(message = "Lastname required")
	String lastName;
	@Column(name = "BIRTHDATE", nullable = false)
	@Temporal(TemporalType.DATE)
	@Past
	Date birthdate;
	@Enumerated(EnumType.STRING)
	Gender gender ;

	@Column(name = "USERNAME", nullable = false)
	@NotBlank(message = "Username required")
	String username;
	@Column(name = "EMAIL", nullable = false)
	@NotBlank(message = "Email required")
	@Email
	String email;
	@Column(name = "PASSWORD", length = 100, nullable = false)
	@NotBlank(message = "Password required")
	@Size(min = 8, max = 60, message = "password must have 8 to 20 caracters.")
	String password;
	@Column(name = "ROLE", nullable = false)
	@Enumerated(EnumType.STRING)
	Role role;
	@Column(name = "LOCKED", nullable = false)
	Boolean locked = false;
	@Column(name = "ENABLED", nullable = false)
	Boolean enabled = false;
	@Column(name = "RESET_PASSWORD_TOKEN", nullable = true)
	String resetPasswordToken;

	@Enumerated
	AuthentificationProvider provider;

	Long followedPlanId;

	String checkForPassword;

	String subjectId ;

	@OneToMany
	List<Ingredient> dislikes;

	@OneToMany(cascade=CascadeType.ALL,mappedBy = "user")
	@JsonIgnore
	List<Token> tokens;


	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonIgnore
	Set<Recipe> favoris;

	@OneToMany(mappedBy="user",cascade = CascadeType.ALL)
	Set<PlanOfWeek> plansOfWeek;

	@OneToMany(cascade = CascadeType.ALL)
	Set<RecipePersonnalised> recipiesPersonnalised;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonIgnore
	private MaleBodyInfo maleBodyInfo;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonIgnore
	private FemaleBodyInfo femaleBodyInfo;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonIgnore
	private GroceryList groceryList;


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getAuthority());
		return Collections.singletonList(authority);
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !locked;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
	@OneToOne(cascade = CascadeType.ALL)
	Profile profile;
}