package nl.javalon.sketchlab.resource;

import static nl.javalon.sketchlab.service.RandomStringGeneratorService.Alphabet.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.InternalUserActivationTokenDetailsDao;
import nl.javalon.sketchlab.dao.InternalUserDetailsDao;
import nl.javalon.sketchlab.dao.UserDetailsDao;
import nl.javalon.sketchlab.dto.user.internal.InternalUserCreateDto;
import nl.javalon.sketchlab.dto.user.internal.InternalUserDetailsCreationDto;
import nl.javalon.sketchlab.dto.user.UserActivationDto;
import nl.javalon.sketchlab.entity.tables.pojos.InternalUserActivationToken;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.EntityExistsException;
import nl.javalon.sketchlab.exception.MalformedRequestException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.security.UserRole;
import nl.javalon.sketchlab.service.MailService;
import nl.javalon.sketchlab.service.RandomStringGeneratorService;
import nl.javalon.sketchlab.service.SketchlabPropertiesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;

/**
 * Resource enabling the registration of new internal users. Accessible by anyone.
 *
 * @author Jelle Stege
 */
@SketchlabResource
@RequestMapping(ApiConfig.USER_INTERNAL_REGISTRATION)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "User registration for non-SSO users")
public class UserRegistrationResource {
	public static final String ACTIVATION_LINK_FORMAT = 
			"%s/account/register/activate?user=%s&token=%s";
	
	private final UserDetailsDao userDao;
	private final InternalUserDetailsDao internalUserDao;
	private final InternalUserActivationTokenDetailsDao activationTokenDao;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;
	private final SketchlabPropertiesMapper properties;

	/**
	 * Registers a new internal user. Creates an unactivated account. To activate the account, the user
	 * has to use the userId and the token provided in an email to activate the account.
	 * <p>
	 * This method will create a new user, a new internal user and send the activation email.
	 *
	 * @param requestBody The needed information to create a new user.
	 * @param locale      The locale to use for the activation email, provided by Spring.
	 * @return The created user.
	 */
	@ApiOperation(value = "Register a new user.", notes = " Will create a new inactive user, the " +
			"user has to activate this account by means of an activation email. This method " +
			"ignores the 'active' and 'suspended' fields and will set those fields to false.")
	@PostMapping
	public User post(@RequestBody @Valid InternalUserCreateDto requestBody, Locale locale) {
		if (userDao.findByEmail(requestBody.getEmail(), false) != null) {
			throw new EntityExistsException("Given email is already in use.");
		}
		List<String> blockedDomains = properties.getInternalUser().getRegistration().getBlockedDomains();
		if (blockedDomains.stream().anyMatch(s -> requestBody.getEmail().endsWith(s))) {
			throw new MalformedRequestException("Given email domain blocked from registration.");
		}

		//Create a new user.
		User user = new User();
		user.setFirstName(requestBody.getFirstName());
		user.setLastName(requestBody.getLastName());
		user.setEmail(requestBody.getEmail());
		user.setRole(UserRole.STUDENT.toString());

		//Create a new unactive, unsuspended internal user, needs a hashed password.
		InternalUserDetailsCreationDto internalUser = new InternalUserDetailsCreationDto();
		internalUser.setPasswordHashed(passwordEncoder.encode(requestBody.getPassword()));
		internalUser.setActive(false);
		internalUser.setSuspended(false);
		internalUser.setUser(user);

		//Add the user
		user = internalUserDao.insertAndGet(internalUser);

		//Generate an activation token for the activation process.
		String activationToken = RandomStringGeneratorService.generate(100, LOWERCASE, UPPERCASE, NUMERIC);

		InternalUserActivationToken userActivationToken = new InternalUserActivationToken(user.getId(), activationToken);
		activationTokenDao.insert(userActivationToken);

		String activationLink = String.format(ACTIVATION_LINK_FORMAT,
				properties.getRootUrl(), user.getId(), activationToken);
		this.mailService.sendActivationEmail(user, activationLink, locale);

		return user;
	}

	/**
	 * Activates the user when the given token belongs to the given user ID. Throws a 404 when
	 * the user ID is not found.
	 *
	 * @param activationDto An object with user ID and token fields.
	 */
	@ApiOperation("Activate new user")
	@PostMapping("/activate")
	public void activate(@RequestBody @Valid UserActivationDto activationDto) {
		InternalUserActivationToken activation = NoSuchEntityException.checkNull(
				activationTokenDao.findByIdAndToken(activationDto.getUserId(), activationDto.getToken()),
				"No such activation");

		activationTokenDao.deleteById(activation.getUserId());
		internalUserDao.activateUser(activationDto.getUserId());
	}
}

