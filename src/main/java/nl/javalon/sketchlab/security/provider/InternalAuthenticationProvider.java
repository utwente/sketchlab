package nl.javalon.sketchlab.security.provider;

import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.dao.UserDetailsDao;
import nl.javalon.sketchlab.entity.tables.daos.InternalUserDao;
import nl.javalon.sketchlab.entity.tables.pojos.InternalUser;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoAccessException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.security.authentication.InternalUserAuthentication;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import nl.javalon.sketchlab.service.RandomStringGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Authentication provider for internal students. Does nothing.
 * @author Lukas Miedema
 */
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class InternalAuthenticationProvider implements AuthenticationProvider {

	public static final int GENERATED_PASSWORD_LENGTH = 16;

	private final PasswordEncoder passwordEncoder;
	private final InternalUserDao internalUserDao;
	private final UserDetailsDao userDao;

	@Override
	public boolean isValid(HttpServletRequest request, UserAuthentication authentication) {
		return true; // no way to invalidate like this.
	}

	/**
	 * Login the given user if the plaintext password matches. This will throw an exception if it doesn't.
	 * @param user The user to login
	 * @param plainTextPassword The password to use for the login attempt
	 * @throws NoAccessException if the password does not match.
	 * @throws NoSuchEntityException if the user is not an external user.
	 */
	public void tryLogin(User user, String plainTextPassword) {
		InternalUser internalUser =
				NoSuchEntityException.checkNull(internalUserDao.findById(user.getId()), "No such user");
		if (!internalUser.getActive()) {
			throw new NoAccessException("Account not activated");
		} else if (internalUser.getSuspended()) {
			throw new NoAccessException("Account suspended");
		} else if (!this.passwordEncoder.matches(plainTextPassword, internalUser.getPasswordHashed())) {
			throw new NoAccessException("Wrong password");
		}

		// Login the user
		UserAuthentication authentication = new InternalUserAuthentication(user);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userDao.updateLastLogin(user.getId(), System.currentTimeMillis());
	}

	/**
	 * Generate a new password for the user and store this in the database.
	 * @param user The user to reset the the password for.
	 * @return the new, plaintext password
	 */
	public String resetPassword(InternalUser user) {
		String newPassword = RandomStringGeneratorService.generate(GENERATED_PASSWORD_LENGTH);

		user.setPasswordHashed(this.passwordEncoder.encode(newPassword));
		this.internalUserDao.update(user);
		return newPassword;
	}
}
