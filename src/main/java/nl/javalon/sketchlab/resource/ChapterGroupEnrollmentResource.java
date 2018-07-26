package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.*;
import nl.javalon.sketchlab.dto.chapter.EnrollmentDetailsDto;
import nl.javalon.sketchlab.dto.chapter.EnrollmentUpdateDto;
import nl.javalon.sketchlab.dto.chapter.UserChapterEnrollmentDto;
import nl.javalon.sketchlab.entity.tables.pojos.Enrollment;
import nl.javalon.sketchlab.entity.tables.pojos.SubgroupEnrollment;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.exception.UnprocessableEntityException;
import nl.javalon.sketchlab.security.UserRole;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * Handles management of chapter group enrollments.
 *
 * @author Melcher Stikkelorum
 */
@SketchlabResource
@RequestMapping(ApiConfig.CHAPTER_GROUP_ENROLLMENT)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Management resource for chapter group enrollments")
public class ChapterGroupEnrollmentResource {
	private final EnrollmentDetailsDao enrollmentDao;
	private final NotificationDetailsDao notificationDetailsDao;
	private final SubgroupEnrollmentDetailsDao subgroupEnrollmentDao;
	private final ChapterGroupDetailsDao chapterGroupDao;
	private final UserDetailsDao userDetailsDao;

	/**
	 * Returns all enrollments for a given chapter group.
	 *
	 * @param chapterGroupId The ID of the chapter group.
	 * @param chapterId      The ID of the chapter the chapter group belongs to.
	 * @param authentication the authentication to use, needed for determining whether to generate
	 *                       the friendly_id field.
	 * @return A List of all enrollments, as {@link UserChapterEnrollmentDto} objects.
	 */
	@ApiOperation("Get all enrollments for a given chapter group")
	@GetMapping
	public List<UserChapterEnrollmentDto> getAll(
			@PathVariable int chapterGroupId, @PathVariable int chapterId,
			UserAuthentication authentication) {
		return enrollmentDao.fetchUsersByChapterGroup(
				chapterGroupId, chapterId, authentication.isTeacher());
	}

	/**
	 * Returns a specific enrollment for the given user.
	 *
	 * @param chapterGroupId The ID of the chapter group.
	 * @param chapterId      The ID of the chapter the chapter group belongs to.
	 * @param userId         The ID of the user to fetch the enrollment for.
	 * @param loggedInUser   The currently logged in user.
	 * @return An enrollment as a {@link Enrollment}.
	 */
	@ApiOperation("Get a specific enrollment for the given user in the given chapter group")
	@GetMapping("/{userId}")
	public EnrollmentDetailsDto get(
			@PathVariable int chapterGroupId,
			@PathVariable int chapterId,
			@PathVariable UUID userId,
			@AuthenticationPrincipal User loggedInUser) {
		return NoSuchEntityException.checkNull(
				enrollmentDao.findByIdAndChapterId(chapterGroupId, chapterId, userId, loggedInUser),
				"No such enrollment"
		);
	}

	/**
	 * Updates or creates an enrollment for the given parameters.
	 *
	 * @param chapterGroupId      The ID of the chapter group the enrollment belongs to.
	 * @param chapterId           The ID of the chapter the enrollment belongs to.
	 * @param userId              The ID of the user the enrollment belongs to.
	 * @param loggedInUser        The currently logged in user
	 * @param enrollmentUpdateDto The parameters of the enrollment to update or create.
	 * @return The updated enrollment.
	 */
	@ApiOperation("Update or create enrollment for the given user in the given chapter group")
	@PutMapping("/{userId}")
	public Enrollment put(
			@PathVariable int chapterGroupId,
			@PathVariable int chapterId,
			@PathVariable UUID userId,
			@AuthenticationPrincipal User loggedInUser,
			@RequestBody @Valid EnrollmentUpdateDto enrollmentUpdateDto) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group"
		);
		final User user = NoSuchEntityException.checkNull(
				userDetailsDao.findById(userId),
				"No such user."
		);
		// Teachers should not be enrolled, fail if the given user is a teacher.
		if (user.getRole() != null && user.getRole().equals(UserRole.TEACHER.getRole())) {
			throw new UnprocessableEntityException("Can not enroll teacher.");
		}
		Enrollment enrollment = enrollmentDao
				.findByIdAndChapterId(chapterGroupId, chapterId, userId, loggedInUser);
		final boolean newEntity = enrollment == null;

		if (newEntity) {
			// Create entity
			enrollment = new Enrollment();
			enrollment.setChapterGroupId(chapterGroupId);
			enrollment.setUserId(userId);
		}

		// Check whether the grade is 'upGraded'
		final BigDecimal oldGrade = enrollment.getGrade();
		final BigDecimal dtoGrade = enrollmentUpdateDto.getGrade();
		boolean upgrade = false;
		if ((oldGrade == null && dtoGrade != null)
				|| (oldGrade != null && dtoGrade == null)
				|| (dtoGrade != null && !dtoGrade.equals(oldGrade))) {
			upgrade = true;
		}

		if (enrollmentUpdateDto.getGrade() != null) {
			enrollment.setGrade(enrollmentUpdateDto.getGrade());
			enrollment.setGradedAt(new Timestamp(System.currentTimeMillis()));
			enrollment.setGradeMessage(enrollmentUpdateDto.getGradeMessage());
		}
		if (enrollmentUpdateDto.getAssistant() != null) {
			enrollment.setAssistant(enrollmentUpdateDto.getAssistant());
		}

		if (newEntity) {
			enrollmentDao.insert(enrollment);
			notificationDetailsDao.handleChapterGroupEnrollEvent(chapterGroupId, userId);
		} else {
			enrollmentDao.update(enrollment);
			// Check if grade was changed in this update
			if (upgrade) {
				notificationDetailsDao.handleChapterGroupGradeEvent(chapterGroupId, userId);
			}
		}
		return enrollment;
	}

	/**
	 * Deletes an enrollment. Note that this also deletes all subgroup enrollments, submissions,
	 * annotations, questions and answers made by the given user, as these are reliant on an
	 * enrollment.
	 *
	 * @param chapterGroupId The ID of the chapter group the enrollment belongs to.
	 * @param chapterId      The ID of the chapter the enrollment belongs to.
	 * @param userId         The ID of the user the enrollment belongs to.
	 * @param loggedInUser   The currently logged in user
	 */
	@ApiOperation("Delete an enrollment and all related data. This is destructive.")
	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@PathVariable int chapterGroupId,
			@PathVariable int chapterId,
			@PathVariable UUID userId,
			@AuthenticationPrincipal User loggedInUser) {
		// Check if the enrollment exists.
		this.get(chapterGroupId, chapterId, userId, loggedInUser);

		enrollmentDao.deleteById(chapterGroupId, userId);

		// Also delete all subgroups. Since these are not bound to chapter group enrollments, these
		// have to be deleted manually..
		List<SubgroupEnrollment> subgroups = subgroupEnrollmentDao
				.fetchByChapterGroupIdAndUserId(chapterGroupId, chapterId, userId);
		subgroupEnrollmentDao.delete(subgroups);
	}
}
