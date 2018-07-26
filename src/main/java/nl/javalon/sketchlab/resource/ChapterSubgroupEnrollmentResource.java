package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.ChapterSubgroupDetailsDao;
import nl.javalon.sketchlab.dao.EnrollmentDetailsDao;
import nl.javalon.sketchlab.dao.SubgroupEnrollmentDetailsDao;
import nl.javalon.sketchlab.dto.chapter.UserChapterEnrollmentDto;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterSubgroup;
import nl.javalon.sketchlab.entity.tables.pojos.SubgroupEnrollment;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.exception.UnprocessableEntityException;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Handles management of chapter subgroup enrollments.
 *
 * @author Melcher Stikkelorum
 */
@SketchlabResource
@RequestMapping(ApiConfig.CHAPTER_SUBGROUP_ENROLLMENT)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Management resource for chapter subgroup enrollments")
public class ChapterSubgroupEnrollmentResource {
	private final SubgroupEnrollmentDetailsDao subgroupEnrollmentDao;
	private final ChapterSubgroupDetailsDao subgroupDetailsDao;
	private final EnrollmentDetailsDao chapterGroupEnrollmentDao;

	/**
	 * Returns a list of all enrolled users in a subgroup.
	 *
	 * @param chapterSubgroupId The ID of the subgroup.
	 * @param chapterGroupId    The ID of the chapter group the subgroup belongs to.
	 * @param chapterId         The ID of the chapter the subgroup belongs to.
	 * @param authentication    The {@link UserAuthentication} used by the logged in user, necessary
	 *                          to determine whether the given user may see the user's friendly ID.
	 * @return A list of all enrollments.
	 */
	@ApiOperation("Get all enrollments for a given chapter subgroup")
	@GetMapping
	public List<UserChapterEnrollmentDto> getAll(
			@PathVariable int chapterSubgroupId,
			@PathVariable int chapterGroupId,
			@PathVariable int chapterId, 
			UserAuthentication authentication) {
		return subgroupEnrollmentDao.fetchUsersByChapterSubgroupId(
				chapterSubgroupId, chapterGroupId, chapterId, authentication.isTeacher());
	}

	/**
	 * Creates (or updates) a subgroup enrollment for the given user in the given chapter subgroup.
	 * Checks if there is room in the subgroup, since they may be space limited.
	 *
	 * @param chapterSubgroupId The ID of the subgroup to enroll to.
	 * @param chapterGroupId    The ID of the chapter group the subgroup belongs to.
	 * @param chapterId         The ID of the chapter the subgroup belongs to.
	 * @param userId            The ID of the user to enroll.
	 * @return The created subgroup enrollment as a {@link SubgroupEnrollment}.
	 */
	@ApiOperation("Update or create enrollment for the given user in the given chapter subgroup")
	@PutMapping("/{userId}")
	public SubgroupEnrollment put(
			@PathVariable int chapterSubgroupId,
			@PathVariable int chapterGroupId,
			@PathVariable int chapterId,
			@PathVariable UUID userId) {
		ChapterSubgroup subgroup = NoSuchEntityException.checkNull( 
				subgroupDetailsDao.findByIdAndChapterGroupAndChapter(
						chapterSubgroupId, chapterGroupId, chapterId),
				"No such subgroup"
		);
		// Teachers should not be inrolled in subgroups, but they also should not be enrolled
		// in chapter groups, therefore, this rule can not be true.
		NoSuchEntityException.checkNull(
				chapterGroupEnrollmentDao.findById(chapterGroupId, userId),
				"No such user in this chapter group"
		);
		int enrolledUserCount = subgroupEnrollmentDao
				.fetchUsersByChapterSubgroupId(chapterSubgroupId, chapterGroupId, chapterId, false)
				.size();
		if (subgroup.getSize() != null && subgroup.getSize() <= enrolledUserCount) {
			throw new UnprocessableEntityException("Subgroup is at capacity.");
		}

		SubgroupEnrollment subgroupEnrollment = subgroupEnrollmentDao
				.findByIdAndChapterId(chapterSubgroupId, chapterGroupId, chapterId, userId);
		boolean newEntity = subgroupEnrollment == null;

		if (newEntity) {
			// Create entity
			subgroupEnrollment = new SubgroupEnrollment();
			subgroupEnrollment.setChapterSubgroupId(chapterSubgroupId);
			subgroupEnrollment.setUserId(userId);
			subgroupEnrollmentDao.insert(subgroupEnrollment);
		}

		return subgroupEnrollment;
	}

	/**
	 * Deletes a user enrollment from a subgroup.
	 *
	 * @param chapterSubgroupId The ID of the subgroup.
	 * @param chapterGroupId    The ID of the chapter group the subgroup belongs to.
	 * @param chapterId         The ID of the chapter the subgroup belongs to.
	 * @param userId            The ID of the user to disenroll.
	 */
	@ApiOperation("Delete a subgroup enrollment and all related data. This is destructive.")
	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@PathVariable int chapterSubgroupId,
			@PathVariable int chapterGroupId,
			@PathVariable int chapterId,
			@PathVariable UUID userId) {
		if (!subgroupEnrollmentDao
				.existsById(chapterSubgroupId, chapterGroupId, chapterId, userId)) {
			throw new NoSuchEntityException("No such chapter subgroup enrollment");
		}
		subgroupEnrollmentDao.deleteById(chapterSubgroupId, userId);
	}
}
