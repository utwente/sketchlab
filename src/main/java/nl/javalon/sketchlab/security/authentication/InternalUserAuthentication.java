package nl.javalon.sketchlab.security.authentication;

import lombok.NonNull;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.security.provider.AuthenticationProvider;
import nl.javalon.sketchlab.security.provider.InternalAuthenticationProvider;

/**
 * @author Lukas Miedema
 */
public class InternalUserAuthentication extends UserAuthentication {

	/**
	 * Create a new authentication for a user.
	 *
	 * @param principal the user.
	 */
	public InternalUserAuthentication(@NonNull User principal) {
		super(principal);
	}

	@Override
	public Class<? extends AuthenticationProvider> getProviderClass() {
		return InternalAuthenticationProvider.class;
	}
}
