package nl.javalon.sketchlab.security.authentication;

import lombok.Getter;
import lombok.NonNull;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.security.UserRole;
import nl.javalon.sketchlab.security.provider.AuthenticationProvider;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

/**
 * @author Lukas Miedema
 */
@Getter
public abstract class UserAuthentication extends AbstractAuthenticationToken {

	private final User principal;
	private final UserRole role;

	/**
	 * Create a new authentication for a user.
	 *
	 * @param principal the user.
	 */
	public UserAuthentication(@NonNull User principal) {
		super(UserRole.valueOf(principal.getRole()).getRoles());
		setAuthenticated(true);

		UserRole role = UserRole.valueOf(principal.getRole());
		this.principal = principal;
		this.role = role;
	}

	/**
	 * @return The class that can provide and validate this authentication.
	 */
	@NonNull
	public abstract Class<? extends AuthenticationProvider> getProviderClass();

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public User getPrincipal() {
		return principal;
	}

	public boolean isStudent() {
		return this.role == UserRole.STUDENT;
	}

	public boolean isAnonymous() {
		return this.role == UserRole.ANONYMOUS;
	}

	public boolean isTeacher() {
		return this.role == UserRole.TEACHER;
	}
}
