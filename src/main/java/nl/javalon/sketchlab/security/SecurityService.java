package nl.javalon.sketchlab.security;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import nl.javalon.sketchlab.dao.EnrollmentDetailsDao;
import nl.javalon.sketchlab.dao.UserDetailsDao;
import nl.javalon.sketchlab.entity.tables.pojos.Enrollment;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Context-dependent permissions
 *
 * @author Lukas Miedema
 */
@Service("security")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class SecurityService {

	public static final UUID ANONYMOUS_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

	private final EnrollmentDetailsDao enrollmentDao;
	private final UserDetailsDao userDetailsDao;

	/**
	 * Enumeration of all permissions in the system.
	 */
	public enum Permission {
		ANYONE,
		ANONYMOUS,
		TEACHER,
		STUDENT,
		TA
	}

	/**
	 * Checks the given user ID to the stored principal's ID
	 *
	 * @param userId      The given user ID
	 * @param principalId The stored principal's ID
	 * @return True if equal, false if otherwise.
	 */
	public boolean compare(UUID userId, UUID principalId) {
		return userId.equals(principalId);
	}

	/**
	 * Check if the principal has access to a chapter via any of its enrollments.
	 *
	 * @param principal         the user.
	 * @param chapterId         the chapter id.
	 * @param permissionStrings a list of permissions.
	 * @return true if the user has permission, false otherwise.
	 */
	public boolean hasPermissionToChapter(@NonNull User principal, int chapterId, @NonNull String... permissionStrings) {
		List<Permission> permissions = Arrays.stream(permissionStrings)
				.map(Permission::valueOf).collect(Collectors.toList());

		if (permissions.contains(Permission.ANYONE)) {
			return true;
		}

		if (UserRole.valueOf(principal.getRole()) == UserRole.TEACHER && permissions.contains(Permission.TEACHER)) {
			return true;
		}

		// Fetch all enrollments of this user
		List<Enrollment> enrollments = enrollmentDao.fetchByUserAndChapterId(principal.getId(), chapterId);
		return enrollments.stream().anyMatch(e -> hasPermission(principal, e, permissions));
	}

	/**
	 * Checks if the principal has any of the permissions for the given enrollment.
	 *
	 * @param principal      the principal to use.
	 * @param chapterGroupId the chapter group context.
	 * @param permissions    the permissions.
	 * @return true if the principal has any of the permissions.
	 */
	public boolean hasPermission(@NonNull User principal, int chapterGroupId, @NonNull String... permissions) {
		Enrollment enrollment = enrollmentDao.findById(chapterGroupId, principal.getId());
		return this.hasPermission(principal, enrollment, permissions);
	}

	/**
	 * Checks if the principal has any of the permissions for the given enrollment.
	 *
	 * @param principal         the principal to use.
	 * @param enrollment        the enrollment context (can be null if no enrollment).
	 * @param permissionStrings the permissions.
	 * @return true if the principal has any of the permissions.
	 */
	public boolean hasPermission(@NonNull User principal, Enrollment enrollment, @NonNull String... permissionStrings) {
		List<Permission> permissions = Arrays.stream(permissionStrings)
				.map(Permission::valueOf).collect(Collectors.toList());
		return this.hasPermission(principal, enrollment, permissions);
	}

	/**
	 * Checks if the principal has any of the permissions for the given enrollment.
	 *
	 * @param principal   the principal to use.
	 * @param enrollment  the enrollment context (can be null if no enrollment).
	 * @param permissions the permissions.
	 * @return true if the principal has any of the permissions.
	 */
	public boolean hasPermission(@NonNull User principal, Enrollment enrollment, @NonNull List<Permission> permissions) {
		if (permissions.contains(Permission.ANYONE)) {
			return true;
		}
		if (permissions.contains(Permission.TEACHER) && principal.getRole().equals(UserRole.TEACHER.getRole())) {
			return true;
		}

		// Whenever an enrollment was found, make sure that the principal's role in the system lines up with the needed permission
		if (enrollment != null) {
			if (permissions.contains(Permission.STUDENT) && principal.getRole().equals(Permission.STUDENT.toString()) && !enrollment.getAssistant()) {
				return true;
			}
			if (permissions.contains(Permission.ANONYMOUS) && principal.getRole().equals(Permission.ANONYMOUS.toString()) && !enrollment.getAssistant()) {
				return true;
			}
			if (permissions.contains(Permission.TA) && principal.getRole().equals(Permission.STUDENT.toString()) && enrollment.getAssistant()) {
				return true;
			}
		}

		// No enrollment was found or permission not granted
		return false;
	}
}
