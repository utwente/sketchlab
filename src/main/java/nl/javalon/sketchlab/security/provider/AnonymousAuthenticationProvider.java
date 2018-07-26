package nl.javalon.sketchlab.security.provider;

import nl.javalon.sketchlab.security.authentication.AnonymousUserAuthentication;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Always provide the {@link AnonymousUserAuthentication}. This is registered with the lowest precedence, such that
 * any implementation gets a chance to authenticate first. It also reports any instance a "invalid" to prompt looking
 * for another authentication.
 * @author Lukas Miedema
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class AnonymousAuthenticationProvider implements AuthenticationProvider {

	@Override
	public UserAuthentication provide(HttpServletRequest request) {
		return AnonymousUserAuthentication.INSTANCE;
	}

	@Override
	public boolean isValid(HttpServletRequest request, UserAuthentication authentication) {
		return false;
	}
}
