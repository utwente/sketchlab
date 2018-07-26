package nl.javalon.sketchlab.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

/**
 * @author Lukas Miedema
 */
public enum UserRole implements GrantedAuthority {

	TEACHER(new SimpleGrantedAuthority("ACTUATOR")),
	STUDENT,
	ANONYMOUS;

	private final Collection<GrantedAuthority> roles;
	UserRole(GrantedAuthority... roles) {
		List<GrantedAuthority> allRoles = new ArrayList<>(Arrays.asList(roles));
		allRoles.add(this);
		this.roles = Collections.unmodifiableCollection(allRoles);
	}

	/**
	 * @return the role as authority (prefixed with ROLE_).
	 */
	@Override
	public String getAuthority() {
		return "ROLE_" + this.name();
	}

	/**
	 * @return the literal name of this.
	 */
	public String getRole() {
		return this.name();
	}

	/**
	 * Get all roles in a collection. This may include additional roles that are needed for certain admin tasks.
	 * @return
	 */
	public Collection<GrantedAuthority> getRoles() {
		return roles;
	}
}
