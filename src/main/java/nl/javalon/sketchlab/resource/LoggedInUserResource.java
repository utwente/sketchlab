package nl.javalon.sketchlab.resource;

import static nl.javalon.sketchlab.config.ApiConfig.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.dao.ChapterChapterGroupSubgroupDao;
import nl.javalon.sketchlab.dao.ChapterGroupDetailsDao;
import nl.javalon.sketchlab.dao.EnrollmentDetailsDao;
import nl.javalon.sketchlab.dto.chapter.ChapterGroupDetailsDto;
import nl.javalon.sketchlab.dto.chapter.ChapterGroupDetailsWithSubgroupsDto;
import nl.javalon.sketchlab.dto.chapter.EnrollmentDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoAccessException;
import nl.javalon.sketchlab.security.authentication.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * REST endpoint which returns all kinds of information the current logged in user might need.
 * This is encased in one single resouce because seperating it causes a lot of classes with a single
 * method.
 *
 * @author Jelle Stege
 */
@SketchlabResource
@AllArgsConstructor(onConstructor = @__({@Autowired}))
@Api(description = "General endpoint for all kinds of information the logged in user might need.")
public class LoggedInUserResource {
	private final ChapterGroupDetailsDao chapterGroupDao;
	private final ChapterChapterGroupSubgroupDao chapterChapterGroupSubgroupDao;
	private final EnrollmentDetailsDao enrollmentDao;

	/**
	 * Returns all chapter groups (including tasks and chapter info) the current user is enrolled
	 * in as a student or teaching assistant, or all chapter groups when the user is a teacher.
	 *
	 * @param user The user for which to retrieve all (very) detailed chapter groups.
	 * @return A List of all chapter groups with all details.
	 */
	@ApiOperation("Get all chapter groups (including tasks and chapter info) when the current " +
			"user is a teacher or all groups where the current user is enrolled in")
	@GetMapping(CHAPTER_GROUP_ME)
	public List<ChapterGroupDetailsDto> getMe(
			UserAuthentication authentication,
			@AuthenticationPrincipal User user) {
		if (authentication.isTeacher()) {
			return chapterGroupDao.fetchChapterDetails();
		}
		return chapterGroupDao.fetchChapterDetailsByUserId(user.getId());
	}

	/**
	 * Returns the enrollment information of all chapter groups the user is enrolled in. Including
	 * chapter group and chapter information.
	 *
	 * @param user      The logged in user.
	 * @param includeTa Whether or not to include TA enrollments
	 * @return A list of all enrollment info for all chapter groups the user is enrolled in.
	 * @throws NoAccessException When the user is the anonymous user.
	 */
	@ApiOperation("Returns all enrollments for the given user. Anonymous user excluded.")
	@GetMapping(CHAPTER_GROUP_ENROLLMENT_ME)
	public List<EnrollmentDetailsDto> getAllForUser(
			@AuthenticationPrincipal User user,
			@RequestParam(defaultValue = "false") boolean includeTa) {
		return enrollmentDao.fetchByUser(user.getId(), includeTa, user);
	}

	/**
	 * Returns a listing of all chapters with their belonging chapter groups and
	 * the subgroups the user is enrolled in. Or all chapters when the user is a teacher.
	 *
	 * @param user The current principal user.
	 * @return A listing of all chapters with their belonging chapter groups and
	 * the subgroups the user is enrolled in.
	 */
	@GetMapping(CHAPTER_SUBGROUP_ME)
	public List<ChapterGroupDetailsWithSubgroupsDto> get(
			@AuthenticationPrincipal User user, UserAuthentication authentication) {
		if (authentication.isTeacher()) {
			return chapterChapterGroupSubgroupDao.fetchAll();
		}
		return chapterChapterGroupSubgroupDao.fetchAllByUserId(user.getId());
	}
}
