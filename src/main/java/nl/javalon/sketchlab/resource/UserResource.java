package nl.javalon.sketchlab.resource;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.EnrollmentDetailsDao;
import nl.javalon.sketchlab.dao.SubgroupEnrollmentDetailsDao;
import nl.javalon.sketchlab.dao.UserDetailsDao;
import nl.javalon.sketchlab.dto.user.UserDetailsDto;
import nl.javalon.sketchlab.dto.user.UserSearchType;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.exception.UnprocessableEntityException;
import nl.javalon.sketchlab.security.SecurityService;
import nl.javalon.sketchlab.security.UserRole;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoint for general user related operations
 *
 * @author Lukas Miedema
 */
@SketchlabResource
@RequestMapping(ApiConfig.USER)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserResource {

	public static final int MAX_USER_RETURN = 200;
	private final UserDetailsDao userDao;
	private final EnrollmentDetailsDao enrollmentDao;
	private final SubgroupEnrollmentDetailsDao subgroupEnrollmentDao;


	/**
	 * Retrieves a specific user.
	 *
	 * @param userId         The ID of the user to retrieve
	 * @param authentication The authentication used by the currently logged in user, this is
	 *                       needed so the DAO can determine whether or not to include the
	 *                       "friendly_id" field, which may only be seen by Teachers.
	 * @return The specific user, or null if non existent.
	 */
	@ApiOperation(value = "Get one user", notes = "Allows Anyone")
	@GetMapping("/{userId}")
	public User get(@PathVariable("userId") UUID userId, UserAuthentication authentication) {
		return NoSuchEntityException.checkNull(
				userDao.findById(userId, authentication.isTeacher()),
				"No such user"
		);
	}

	/**
	 * Updates the role of a user. Note that when the user is made teacher, all previous chapter
	 * group enrollments are removed, along with all associated data.
	 *
	 * @param userId The ID of the user for which to update.
	 * @param role   The new role of the user.
	 * @return The newly updated user.
	 */
	@ApiOperation(value = "Update the role of a user and return that user",
			notes = "Only allows TEACHER")
	@PutMapping("/{userId}/role")
	public User updateRole(
			@PathVariable("userId") UUID userId, @NotNull @Valid @RequestParam UserRole role) {
		if (userId.equals(SecurityService.ANONYMOUS_USER_ID)) {
			throw new UnprocessableEntityException("Cannot change the role of the anonymous user");
		}
		if (role == null || role == UserRole.ANONYMOUS) {
			throw new UnprocessableEntityException(
					"Non-anonymous user cannot take a non-existing role, nor the anonymous role"
			);
		}
		User user = NoSuchEntityException.checkNull(userDao.fetchOneById(userId), "No such user");
		user.setRole(role.toString());
		userDao.update(user);

		//If new role is teacher, also delete all enrollments and subgroup enrollments.
		if (role == UserRole.TEACHER) {
			enrollmentDao.deleteAllForUser(user.getId());
			subgroupEnrollmentDao.deleteAllForUser(user.getId());
		}

		return user;
	}

	/**
	 * Finds users by first name, last name, email or UT id.
	 *
	 * @param authentication  The authentication of the logged in user, to determine whether the
	 *                        user is a teacher.
	 * @param userSearchType  The type of user to search for: all users, internal users only or
	 *                        UT users only.
	 * @param limit           The maximum amount of users to return, with an upper limit equal to
	 *                        {@link UserResource#MAX_USER_RETURN}.
	 * @param includeInactive Whether or not to users which have been deactivated.
	 * @param searchInput     The search query to actually search for.
	 * @return All users (amount limited to the given limit parameter) which adhere to the given
	 * search query. All returned users have the search query in either their first name, last name,
	 * email address or UT id.
	 */
	@ApiOperation(value = "Find users by name",
			notes = "Searches the database in a case insensitive manner. This resource will " +
					"search for matches in the first name, last name, email and UTwente student " +
					"numbers. Note that this resource will never return more than 200 users.")
	@GetMapping("/by-name")
	public List<UserDetailsDto> findUser(
			UserAuthentication authentication,
			@RequestParam(
					name = "user-type",
					defaultValue = "ALL"
			) UserSearchType userSearchType,
			@RequestParam(
					name = "limit",
					defaultValue = "100"
			) int limit,
			@RequestParam(
					name = "include-inactive",
					defaultValue = "false"
			) boolean includeInactive,
			@RequestParam(
					name = "search-input",
					defaultValue = "",
					required = false
			) String searchInput
	) {
		return userDao.searchByName(
				searchInput,
				userSearchType,
				includeInactive,
				Math.min(MAX_USER_RETURN, limit),
				authentication.isTeacher());
	}
}
