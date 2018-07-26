package nl.javalon.sketchlab.security.authentication;

import lombok.Getter;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.security.provider.AnonymousAuthenticationProvider;
import nl.javalon.sketchlab.security.provider.AuthenticationProvider;

import java.util.UUID;

/**
 * Singleton representing the anonymous user.
 * @author Lukas Miedema
 */
@Getter
public class AnonymousUserAuthentication extends UserAuthentication {

	public static final AnonymousUserAuthentication INSTANCE = new AnonymousUserAuthentication();

	/**
	 * Constructor is private. Use the singleton instance.
	 */
	private AnonymousUserAuthentication() {
		super(new User(UUID.fromString("00000000-0000-0000-0000-000000000000"), "Anonymous", null,
				null, "ANONYMOUS", null));
	}

	@Override
	public Class<? extends AuthenticationProvider> getProviderClass() {
		return AnonymousAuthenticationProvider.class;
	}
}
