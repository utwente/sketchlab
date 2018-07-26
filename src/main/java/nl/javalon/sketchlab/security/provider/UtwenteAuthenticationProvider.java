package nl.javalon.sketchlab.security.provider;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import nl.javalon.sketchlab.dao.UserDetailsDao;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import nl.javalon.sketchlab.security.authentication.UtwenteUserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Lukas Miedema
 */
@Component
@Log
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UtwenteAuthenticationProvider implements AuthenticationProvider {

	private static final String REMOTE_USER_HEADER = "OAM_REMOTE_USER";
	private static final String REMOTE_USER_ANONYMOUS = "Anonymous"; // REMOTE_USER_HEADER value indicating no user

	private final UserDetailsDao userDao;

	@Override
	public UserAuthentication provide(HttpServletRequest request) {

		// The SSO proxy server puts the user id in the header if the user is logged in
		String utwenteId = request.getHeader(REMOTE_USER_HEADER);

		if (utwenteId == null || utwenteId.equals(REMOTE_USER_ANONYMOUS)) {
			// We cannot provide an authentication
			return null;

		} else {
			// Retrieve the user from the database
			User utwenteUser = this.userDao.findUserByUTwenteId(utwenteId);
			if (utwenteUser == null) {

				// fall back to anonymous
				log.warning("Cannot authenticate Utwente-user '" +
						utwenteId + "' as it's not in the db");
				return null;
			} else {

				// Update last-putMe date
				userDao.updateLastLogin(utwenteUser.getId(), System.currentTimeMillis());

				// Create authentication and return
				return new UtwenteUserAuthentication(utwenteId, utwenteUser);
			}
		}
	}

	@Override
	public boolean isValid(HttpServletRequest request, UserAuthentication authentication) {
		// Check if the SSO id still matches
		String utwenteId = request.getHeader(REMOTE_USER_HEADER);
		return utwenteId != null && ((UtwenteUserAuthentication) authentication).getUtwenteId().equals(utwenteId);
	}
}
