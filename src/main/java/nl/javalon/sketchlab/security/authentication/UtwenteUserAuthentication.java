package nl.javalon.sketchlab.security.authentication;

import lombok.Getter;
import lombok.NonNull;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.security.provider.AuthenticationProvider;
import nl.javalon.sketchlab.security.provider.UtwenteAuthenticationProvider;

/**
 * @author Lukas Miedema
 */
@Getter
public class UtwenteUserAuthentication extends UserAuthentication {

	private final String utwenteId;

	/**
	 * Create a new authentication for a user.
	 *
	 * @param utwenteId the utwente id (s/x/m number) of the user.
	 * @param principal the user.
	 */
	public UtwenteUserAuthentication(@NonNull String utwenteId, @NonNull User principal) {
		super(principal);
		this.utwenteId = utwenteId;
	}

	@Override
	public Class<? extends AuthenticationProvider> getProviderClass() {
		return UtwenteAuthenticationProvider.class;
	}
}
