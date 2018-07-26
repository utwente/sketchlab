package nl.javalon.sketchlab.security;

import lombok.extern.java.Log;
import nl.javalon.sketchlab.security.authentication.AnonymousUserAuthentication;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import nl.javalon.sketchlab.security.provider.AuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Ensures that a not-logged in user automatically becomes the anonymous user.
 * @author Lukas Miedema
 */
@Log
@Component
public class AuthenticationFilter implements Filter {

	private final List<AuthenticationProvider> providers;
	private final Map<Class<? extends AuthenticationProvider>, AuthenticationProvider> providersByClass;

	public AuthenticationFilter(List<AuthenticationProvider> authenticationProviders) {
		this.providers = authenticationProviders;
		this.providersByClass = authenticationProviders
				.stream()
				.collect(Collectors.toMap(AuthenticationProvider::getClass, Function.identity()));
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) servletRequest;
		UserAuthentication current = (UserAuthentication) SecurityContextHolder.getContext().getAuthentication();

		// Check validity
		if (current != null) {
			if (!providersByClass.get(current.getProviderClass()).isValid(request, current)) {
				// invalid --> prompt recreation
				current = null;
			}
		}

		// Create an authentication
		if (current == null) {
			// Try to provide one for the current user
			UserAuthentication authentication = this.providers
					.stream()
					.map(p -> p.provide(request))
					.filter(Objects::nonNull)
					.findFirst().get(); // there will always be one due to the AnonymousAuthenticationProvider

			SecurityContextHolder.getContext().setAuthentication(authentication);

		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}
}
