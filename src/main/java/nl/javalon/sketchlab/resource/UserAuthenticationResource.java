package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.InternalUserDetailsDao;
import nl.javalon.sketchlab.dao.UserDetailsDao;
import nl.javalon.sketchlab.dto.user.EmailPasswordDto;
import nl.javalon.sketchlab.dto.user.internal.InternalUserDetailsDto;
import nl.javalon.sketchlab.dto.user.PasswordDto;
import nl.javalon.sketchlab.entity.tables.pojos.InternalUser;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoAccessException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import nl.javalon.sketchlab.security.provider.InternalAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * @author Jelle Stege
 */
@SketchlabResource
@RequestMapping(ApiConfig.USER_AUTHENTICATION)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "User authentication")
public class UserAuthenticationResource {

	private final UserDetailsDao userDao;
	private final InternalUserDetailsDao internalUserDao;
	private final PasswordEncoder passwordEncoder;

	private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
	private final InternalAuthenticationProvider internalAuthenticationProvider;

	/**
	 * Returns the current user.
	 *
	 * @param me The authenticated principal
	 * @return The user object of the current user
	 */
	@ApiOperation("Retrieves the current user.")
	@GetMapping
	public User getMe(@AuthenticationPrincipal User me) {
		return userDao.findById(me.getId(), true);
	}

	/**
	 * Performs a login attempt by using provided email and password. Logs out the current user, if
	 * applicable.
	 *
	 * @param loginDto An object with email and password fields
	 * @return The logged in user if login attempt was succesful
	 */
	@ApiOperation("Log in with email and password. This will cause the current user to be logged out, if the user is already logged in")
	@PutMapping
	public User putMe(@RequestBody @Valid EmailPasswordDto loginDto) {
		User user = NoSuchEntityException.checkNull(
				userDao.findByEmail(loginDto.getEmail(), false),
				"No such user");
		this.internalAuthenticationProvider.tryLogin(user, loginDto.getPassword());
		return user;
	}

	/**
	 * Logs out the current user.
	 *
	 * @param request        The HTTP request
	 * @param response       The HTTP response
	 * @param authentication The current authentication
	 */
	@ApiOperation("Logs out the current user. If the user is logged in anonymous, this will do nothing")
	@DeleteMapping
	public void deleteMe(HttpServletRequest request, HttpServletResponse response, UserAuthentication authentication) {
		if (authentication != null && !authentication.isAnonymous()) {
			this.logoutHandler.logout(request, response, authentication);
		}
	}

	/**
	 * Changes password for internal user.
	 *
	 * @param user        The user to change the password for.
	 * @param requestBody The old and new password
	 * @return The updated user, without password field.
	 */
	@PutMapping("/change-password")
	public InternalUserDetailsDto changePassword(UserAuthentication userAuthentication,
												 @AuthenticationPrincipal User user,
												 @RequestBody @Valid PasswordDto requestBody) {
		if (userAuthentication.isAnonymous()) {
			throw new NoAccessException("Not logged in");
		}

		InternalUser internalUser = NoSuchEntityException.checkNull(
				internalUserDao.findById(user.getId()),
				"No such internal user"
		);

		if (!passwordEncoder.matches(requestBody.getOldPassword(), internalUser.getPasswordHashed())) {
			throw new NoAccessException("Wrong password");
		}

		internalUser.setPasswordHashed(passwordEncoder.encode(requestBody.getNewPassword()));
		internalUserDao.update(internalUser);
		return internalUserDao.findOneByUserId(internalUser.getUserId());
	}
}
