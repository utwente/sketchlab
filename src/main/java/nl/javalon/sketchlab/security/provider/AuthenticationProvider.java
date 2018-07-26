package nl.javalon.sketchlab.security.provider;

import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Lukas Miedema
 */
@Order(0)
public interface AuthenticationProvider {

	/**
	 * Provide a {@link UserAuthentication} for the request, or null if no authentication can be provided.
	 * Default implementation returns null for every request.
	 * @param request
	 * @return
	 */
	default UserAuthentication provide(HttpServletRequest request) {
		return null;
	}

	/**
	 * Tests if the provided authentication is still valid. The authentication will be an instance that says it's
	 * provider class is this.
	 * @param authentication
	 * @return true if valid, false otherwise.
	 */
	boolean isValid(HttpServletRequest request, UserAuthentication authentication);
}
