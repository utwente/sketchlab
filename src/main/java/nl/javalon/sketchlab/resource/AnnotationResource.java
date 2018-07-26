package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.AnnotationDetailsDao;
import nl.javalon.sketchlab.dao.SubmissionDetailsDao;
import nl.javalon.sketchlab.dto.task.annotation.AnnotationCreateDto;
import nl.javalon.sketchlab.dto.task.annotation.AnnotationDetailsDto;
import nl.javalon.sketchlab.dto.task.annotation.AnnotationUpdateDto;
import nl.javalon.sketchlab.entity.tables.pojos.Annotation;
import nl.javalon.sketchlab.entity.tables.pojos.Submission;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoAccessException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.security.SecurityService;
import nl.javalon.sketchlab.dao.NotificationDetailsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.List;

/**
 * REST endpoints for annotations/comments on submissions.
 *
 * @author Jelle Stege
 */
@SketchlabResource
@RequestMapping(ApiConfig.ANNOTATION)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Annotation resource, used for both annotations and comments on submissions. " +
		"Note that an annotation may contain both a 'drawing' and a text comment.")
public class AnnotationResource {

	private final AnnotationDetailsDao annotationDao;
	private final SubmissionDetailsDao submissionDao;
	private final SecurityService securityService;
	private final NotificationDetailsDao notificationDetailsDao;

	/**
	 * Retrieves all visible annotations for a specific submission. Soft-deleted annotations
	 * are retrieved by this endpoint by specifying the "include-deleted" parameter as true.
	 *
	 * @param user           The user used to retrieve the annotations for, this is used for
	 *                       authentication.
	 * @param chapterId      The ID of the chapter the annotation belongs to.
	 * @param chapterGroupId The ID of the chapter group the annotation belongs to.
	 * @param submissionId   The submission ID for which to retrieve annotations
	 * @param includeDeleted Whether or not to include deleted annotations.
	 * @return A list of all annotations for the given submissions
	 */
	@ApiOperation("Get all annotations for a specific submission")
	@GetMapping
	public List<AnnotationDetailsDto> getAll(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId,
			@ApiParam("Also show soft deleted submissions")
			@RequestParam(
					value = "include-deleted", defaultValue = "false") boolean includeDeleted) {
		includeDeleted = includeDeleted && securityService
				.hasPermission(user, chapterGroupId, "TA", "TEACHER");


		return annotationDao.fetchBySubmissionIdAndChapterGroupId(
				submissionId, chapterGroupId, chapterId, includeDeleted);
	}

	/**
	 * Retrieves a particular annotation belonging to a specific submission. Soft-deleted
	 * annotations are retrieved by this endpoint by specifying the "include-deleted" parameter as
	 * true.
	 *
	 * @param user           The user used to retrieve the annotation with, used for authentication.
	 * @param chapterId      The ID of the chapter the annotation belongs to.
	 * @param chapterGroupId The ID of the chaptergroup for this submission, needed for
	 *                       authentication.
	 * @param submissionId   The ID of the submission.
	 * @param annotationId   The ID for the annotation to retrieve
	 * @param includeDeleted Whether or not to include deleted annotations.
	 * @return An annotation
	 */
	@ApiOperation("Get a particular annotation")
	@GetMapping("/{annotationId}")
	public AnnotationDetailsDto get(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId,
			@PathVariable int annotationId,
			@ApiParam("Also show soft deleted submissions")
			@RequestParam(
					value = "include-deleted", defaultValue = "false") boolean includeDeleted) {
		includeDeleted = includeDeleted && securityService
				.hasPermission(user, chapterGroupId, "TA", "TEACHER");

		return NoSuchEntityException.checkNull(
				annotationDao.findByIdAndSubmissionIdAndChapterGroupId(
						chapterId, chapterGroupId, submissionId, annotationId, includeDeleted),
				"No such annotation.");
	}

	/**
	 * Creates a new annotation for a specific submission.
	 *
	 * @param user           The user which creates this annotation (e.g. the one who's making the
	 *                       request).
	 * @param chapterId      The ID of the chapter the annotation belongs to.
	 * @param chapterGroupId The chaptergroup for this submission, needed for authentication.
	 * @param submissionId   The ID of the submission to create an annotation for.
	 * @param requestBody    The contents of the annotation. Contains a "drawing", "comment" or both
	 *                       fields which represent the annotation.
	 * @return The created annotation.
	 */
	@ApiOperation("Creates an annotation for a specific submission")
	@PostMapping
	public AnnotationDetailsDto post(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId,
			@Valid @RequestBody AnnotationCreateDto requestBody) {
		Submission s = NoSuchEntityException.checkNull(
				submissionDao.findByChapterGroupAndSubmissionId(
						chapterId, chapterGroupId, submissionId, user.getId(), false),
				"No such submission."
		);

		Annotation annotation = new Annotation();
		annotation.setSubmissionId(submissionId);
		annotation.setUserId(user.getId());
		annotation.setDrawing(requestBody.getDrawing());
		annotation.setComment(requestBody.getComment());
		annotation.setSoftDeleted(false);

		AnnotationDetailsDto detailed = annotationDao.insertAndGet(annotation);
		this.notificationDetailsDao.handleSubmissionAnnotationEvent(s, detailed.getId());

		return detailed;
	}

	/**
	 * Updates a previously created annotation, or creates a new one if not present.
	 *
	 * @param user           The user editing/creating the annotation
	 * @param chapterId      The ID of the chapter.
	 * @param annotationId   The ID of the annotation.
	 * @param submissionId   The ID of the submission.
	 * @param chapterGroupId The ID of the belonging chaptergroup, needed for authentication.
	 * @param requestBody    The contents of the annotation.
	 * @return The updated/created annotation.
	 */
	@ApiOperation("Updates an annotation, only works if TA/teacher or student is owner of " +
			"annotation.")
	@PutMapping("/{annotationId}")
	public AnnotationDetailsDto put(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int annotationId,
			@PathVariable int submissionId,
			@PathVariable int chapterGroupId,
			@Valid @RequestBody AnnotationUpdateDto requestBody) {
		AnnotationDetailsDto annotation = NoSuchEntityException.checkNull(
				annotationDao.findByIdAndSubmissionIdAndChapterGroupId(
						chapterId, chapterGroupId, submissionId, annotationId, true),
				"No such annotation");

		final boolean isTaOrTeacher = this.securityService.hasPermission(
				user,
				chapterGroupId,
				SecurityService.Permission.TA.toString(),
				SecurityService.Permission.TEACHER.toString()
		);

		if (!isTaOrTeacher && !user.getId().equals(annotation.getUserId())) {
			throw new NoAccessException("Cannot modify other's annotation.");
		}

		annotation.setSoftDeleted(requestBody.getSoftDeleted());
		annotation.setDrawing(requestBody.getDrawing());
		annotation.setComment(requestBody.getComment());
		annotation.setLastUpdatedAt(new Timestamp(System.currentTimeMillis()));

		annotationDao.update(annotation);

		return annotation;
	}
}
