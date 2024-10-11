package spark.ukla.entities.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
	ADMIN, USER, CREATOR, SUPERADMIN;
	@Override
	public String getAuthority() {
		return "ROLE_" + name();
	}
}
