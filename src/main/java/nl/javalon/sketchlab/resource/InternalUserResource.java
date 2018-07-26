package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.InternalUserDetailsDao;
import nl.javalon.sketchlab.dao.UserDetailsDao;
import nl.javalon.sketchlab.dto.user.internal.InternalUserCreateDto;
import nl.javalon.sketchlab.dto.user.internal.InternalUserDetailsCreationDto;
import nl.javalon.sketchlab.dto.user.internal.InternalUserDetailsDto;
import nl.javalon.sketchlab.dto.user.internal.InternalUserUpdateDto;
import nl.javalon.sketchlab.entity.tables.pojos.InternalUser;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.EntityExistsException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.security.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Management for internal users. Only teachers should have access to this resource.
 *
 * @author Jelle Stege
 */
@SketchlabResource
@Api(description = "Internal user management, teacher only")
@RequestMapping(ApiConfig.USER_INTERNAL_MANAGEMENT)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class InternalUserResource {
	private InternalUserDetailsDao internalUserDao;
	private UserDetailsDao userDao;
	private PasswordEncoder passwordEncoder;

	/**
	 * Returns all internal users in the system
	 *
	 * @return A List of internal users.
	 */
	@ApiOperation(value = "Returns all internal users in the system.")
	@GetMapping
	public List<InternalUserDetailsDto> getAll() {
		return internalUserDao.fetchAllUsers();
	}

	/**
	 * Returns a specific internal user
	 *
	 * @param userId The user ID of the internal user to return
	 * @return The requested internal user, or null if not present.
	 */
	@ApiOperation(value = "Returns a specific internal user")
	@GetMapping("/{userId}")
	public InternalUserDetailsDto get(@PathVariable UUID userId) {
		return internalUserDao.findOneByUserId(userId);
	}

	/**
	 * Creates a new internal user. Unlike
	 * {@link UserRegistrationResource#post(InternalUserCreateDto, Locale)}, this method will not
	 * send an activation email. Activation can be set through the request body.
	 *
	 * @param requestBody The information requried to create a new internal user.
	 * @return The created internal user.
	 */
	@ApiOperation(value = "Creates a new internal user.",
			notes = "Unlike /internal/new this method does not send activation emails. " +
					"Activation can be set through the request body.")
	@PostMapping
	public InternalUserDetailsDto post(@RequestBody @Valid InternalUserCreateDto requestBody) {
		if (!userDao.fetchByEmail(requestBody.getEmail()).isEmpty()) {
			throw new EntityExistsException("Given email is already in use.");
		}


		//Create a new user.
		User user = new User();
		user.setFirstName(requestBody.getFirstName());
		user.setLastName(requestBody.getLastName());
		user.setEmail(requestBody.getEmail());
		user.setRole(UserRole.STUDENT.toString());

		//Create the internal user.
		InternalUserDetailsCreationDto internalUser = new InternalUserDetailsCreationDto();
		internalUser.setPasswordHashed(passwordEncoder.encode(requestBody.getPassword()));
		internalUser.setActive(requestBody.getActive());
		internalUser.setSuspended(requestBody.getSuspended());
		internalUser.setUser(user);

		//Add the user
		user = internalUserDao.insertAndGet(internalUser);
		return internalUserDao.findOneByUserId(user.getId());
	}

	/**
	 * Updates an internal user. 'active', 'suspended' and 'password' fields are not required in the
	 * request body. When updating email field please take note that an email address is considered
	 * unique.
	 *
	 * @param userId      The user ID to update.
	 * @param requestBody The information required to update an internal user.
	 * @return The updated internal user object.
	 */
	@ApiOperation(value = "Updates an internal user.",
			notes = "'active', 'suspended' and 'password' fields are not required. When updating " +
					"email field please take note that an email address is considered unique.")
	@PutMapping("/{userId}")
	public InternalUserDetailsDto put(
			@PathVariable UUID userId,
			@RequestBody @Valid InternalUserUpdateDto requestBody) {
		InternalUser internalUser = NoSuchEntityException.checkNull(
				internalUserDao.findById(userId),
				"No such internal user");

		if (userDao.existsByEmailAndNotByUserId(requestBody.getEmail(), userId)) {
			throw new EntityExistsException("Given email is already in use.");
		}

		User user = userDao.findById(userId);
		user.setFirstName(requestBody.getFirstName());
		user.setLastName(requestBody.getLastName());
		user.setEmail(requestBody.getEmail());

		if (requestBody.getActive() != null) {
			internalUser.setActive(requestBody.getActive());
		}
		if (requestBody.getSuspended() != null) {
			internalUser.setSuspended(requestBody.getSuspended());
		}
		if (requestBody.getPassword() != null) {
			internalUser.setPasswordHashed(passwordEncoder.encode(requestBody.getPassword()));
		}

		return internalUserDao.updateAndGet(new InternalUserDetailsCreationDto(internalUser, user));
	}

	/**
	 * Deletes an internal user. Also deletes the corresponding regular user.
	 *
	 * @param userId The user ID belonging to the user to be deleted.
	 */
	@ApiOperation(value = "Deletes an internal user", notes = "Also deletes the corresponding " +
			"regular user.")
	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID userId) {
		NoSuchEntityException.checkNull(
				internalUserDao.findOneByUserId(userId),
				"No such internal user");
		internalUserDao.deleteUserById(userId);
	}

	/**
	 * Finds internal users by first name, last name or email.
	 *
	 * @param limit       The maximum amount of users to return, with an upper limit equal to
	 *                    {@link UserResource#MAX_USER_RETURN}.
	 * @param searchInput The search query to actually search for.
	 * @return All users (amount limited to the given limit parameter) which adhere to the given
	 * search query. All returned users have the search query in either their first name, last name
	 * or email address.
	 */
	@ApiOperation(value = "Find internal users by name or email",
			notes = "Searches the database in a case insensitive manner. This resource will " +
					"search for matches in the first name, last name and email. " +
					"Note that this resource will never return more than 200 users.")
	@GetMapping("/search")
	public List<InternalUserDetailsDto> findUser(
			@RequestParam(
					name = "limit",
					defaultValue = "100"
			) int limit,
			@RequestParam(
					name = "search-input",
					defaultValue = "",
					required = false
			) String searchInput
	) {
		return internalUserDao.findByPattern(searchInput, limit);
	}
}
