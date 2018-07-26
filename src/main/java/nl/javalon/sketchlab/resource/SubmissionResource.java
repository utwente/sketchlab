package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.*;
import nl.javalon.sketchlab.dto.page.Page;
import nl.javalon.sketchlab.dto.page.PageParameters;
import nl.javalon.sketchlab.dto.task.submission.SubmissionDetailsDto;
import nl.javalon.sketchlab.dto.task.submission.SubmissionOrdering;
import nl.javalon.sketchlab.dto.task.submission.SubmissionUpdateDto;
import nl.javalon.sketchlab.entity.tables.pojos.*;
import nl.javalon.sketchlab.exception.MalformedRequestException;
import nl.javalon.sketchlab.exception.NoAccessException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.security.SecurityService;
import nl.javalon.sketchlab.security.UserRole;
import nl.javalon.sketchlab.service.FileService;
import nl.javalon.sketchlab.service.ImageService;
import nl.javalon.sketchlab.service.ImageService.ImageFormat;
import nl.javalon.sketchlab.service.ImageService.RotationState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoint for submissions.
 *
 * @author Lukas Miedema
 */
@SketchlabResource
@RequestMapping(ApiConfig.SUBMISSION)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Submission resource (including files)")
public class SubmissionResource {
	public static final String DEFAULT_PAGE_SIZE = "15";
	
	private final TaskDetailsDao taskDao;
	private final ChapterGroupDetailsDao chapterGroupDao;
	private final SubmissionDetailsDao submissionDao;
	private final SubmissionFileDetailsDao submissionFileDao;
	private final SubmissionThumbnailDetailsDao submissionThumbnailDao;
	private final VoteDetailsDao voteDao;
	private final AnnotationDetailsDao annotationDetailsDao;
	private final ImageService imageService;
	private final SecurityService securityService;
	private final NotificationDetailsDao notificationDetailsDao;
	private final FileService fileService;

	/**
	 * Returns all submissions for a certain chapter group.
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to fetch submissions for.
	 * @param chapterGroupId The ID of the chapter group to fetch submissions for.
	 * @param ordering       The ordering that should be used.
	 * @param offset         The offset at which to start returning elements.
	 * @param pageSize       The maximum amount of returned elements.
	 * @return A page containing a List of all submissions for the given chapter group.
	 */
	@ApiOperation("Get all submissions")
	@GetMapping
	public Page<SubmissionDetailsDto> getAll(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@RequestParam(value = "ordering", defaultValue = "BEST") SubmissionOrdering ordering,
			@RequestParam(value = "pageOffset") int offset,
			@RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
	) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		return submissionDao.fetchByChapterGroup(
				chapterGroupId,
				user.getId(),
				ordering,
				PageParameters.of(offset, pageSize)
		);
	}

	/**
	 * Returns all submissions for a given user in a given chapter group.
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to fetch submissions for.
	 * @param chapterGroupId The ID of the chapter group to fetch submissions for.
	 * @param userId         The ID of the user to fetch submissions for.
	 * @param ordering       The ordering that should be used.
	 * @param includeDeleted True if soft-deleted submissions should be included. False if not. This
	 *                       parameter is ignored if the logged in user is a student.
	 * @param offset         The offset at which to start returning elements.
	 * @param pageSize       The maximum amount of returned elements.
	 * @return A page containing a List of all submissions for the given user ID and chapter
	 * group ID.
	 */
	@ApiOperation("Get all submissions from a particular user within the chapter group " +
			"(='portfolio'). 'include-deleted' is ignored if the user is a student")
	@GetMapping("/by-user/{userId}")
	public Page<SubmissionDetailsDto> getAllByUser(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable UUID userId,
			@RequestParam(value = "ordering", defaultValue = "BEST") SubmissionOrdering ordering,
			@ApiParam("Include soft deleted submissions in the reply")
			@RequestParam(value = "include-deleted", defaultValue = "false") boolean includeDeleted,
			@RequestParam(value = "pageOffset") int offset,
			@RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
	) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);

		// Set the flag to false if the user is just a student (effectively ignoring it)
		includeDeleted = includeDeleted
				&& securityService.hasPermission(user, chapterGroupId, "TA", "TEACHER");
		return submissionDao.fetchByEnrollment(
				chapterGroupId,
				userId,
				user.getId(),
				ordering,
				includeDeleted,
				PageParameters.of(offset, pageSize)
		);
	}

	/**
	 * Returns all submissions for a given task in a given chapter group.
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to fetch submissions for.
	 * @param chapterGroupId The ID of the chapter group to fetch submissions for.
	 * @param taskId         The ID of the task to fetch submissions for.
	 * @param ordering       The ordering that should be used.
	 * @param offset         The offset at which to start returning elements.
	 * @param pageSize       The maximum amount of returned elements.
	 * @return A page containing a List of all submissions for the given task ID and chapter
	 * group ID.
	 */
	@ApiOperation("Get all submissions for a particular task within the chapter group")
	@GetMapping("/by-task/{taskId}")
	public Page<SubmissionDetailsDto> getAllByTask(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int taskId,
			@RequestParam(value = "ordering", defaultValue = "BEST") SubmissionOrdering ordering,
			@RequestParam(value = "pageOffset") int offset,
			@RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
	) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		return submissionDao.fetchByChapterGroupAndTask(
				chapterGroupId,
				taskId,
				user.getId(),
				ordering,
				PageParameters.of(offset, pageSize)
		);
	}

	/**
	 * Returns all submissions for a given task and user in a given chapter group.
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to fetch submissions for.
	 * @param chapterGroupId The ID of the chapter group to fetch submissions for.
	 * @param taskId         The ID of the task to fetch submissions for.
	 * @param userId         The ID of the user to fetch submissions for.
	 * @param ordering       The ordering that should be used.
	 * @param offset         The offset at which to start returning elements.
	 * @param pageSize       The maximum amount of returned elements.
	 * @return A page containing a List of all submissions for the given task ID, user ID and
	 * chapter group ID.
	 */
	@ApiOperation("Get all submissions for a particular task and user within the chapter group")
	@GetMapping("/by-task/{taskId}/by-user/{userId}")
	public Page<SubmissionDetailsDto> getAllByTaskAndUser(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int taskId,
			@PathVariable UUID userId,
			@RequestParam(value = "ordering", defaultValue = "NEW") SubmissionOrdering ordering,
			@RequestParam(value = "pageOffset") int offset,
			@RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
	) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		return submissionDao.fetchByChapterGroupAndTaskAndUser(
				chapterGroupId,
				taskId,
				userId,
				user.getId(),
				ordering,
				PageParameters.of(offset, pageSize)
		);
	}

	/**
	 * Returns all submissions for a given task in a given chapter group made by the currently
	 * logged in user.
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to fetch submissions for.
	 * @param chapterGroupId The ID of the chapter group to fetch submissions for.
	 * @param taskId         The ID of the task to fetch submissions for.
	 * @param ordering       The ordering that should be used.
	 * @param offset         The offset at which to start returning elements.
	 * @param pageSize       The maximum amount of returned elements.
	 * @return A page containing a List of all submissions for the given task ID and chapter group
	 * ID made by the currently logged in user.
	 */
	@ApiOperation("Get all submissions for a particular task and user within the chapter group")
	@GetMapping("/by-task/{taskId}/me")
	public Page<SubmissionDetailsDto> getAllByTaskAndOwnUser(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int taskId,
			@RequestParam(value = "ordering", defaultValue = "NEW") SubmissionOrdering ordering,
			@RequestParam(value = "pageOffset") int offset,
			@RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize
	) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		return submissionDao.fetchByChapterGroupAndTaskAndUser(
				chapterGroupId,
				taskId,
				user.getId(),
				user.getId(),
				ordering,
				PageParameters.of(offset, pageSize)
		);
	}

	/**
	 * Returns all submissions for a given subgroup in a given chapter group.
	 *
	 * @param user           The ID of the logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to fetch submissions for.
	 * @param chapterGroupId The ID of the chapter group to fetch submissions for.
	 * @param subgroupId     The ID of the subgroup to fetch submissions for.
	 * @param ordering       The ordering that should be used.
	 * @param offset         The offset at which to start returning elements.
	 * @param pageSize       The maximum amount of returned elements.
	 * @return A page containing a List of all submissions for the given subgroup ID and chapter 
	 * group ID.
	 */
	@ApiOperation("Get all submissions for a particular subgroup")
	@GetMapping("/by-subgroup/{subgroupId}")
	public Page<SubmissionDetailsDto> getAllBySubgroup(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int subgroupId,
			@RequestParam(value = "ordering", defaultValue = "BEST") SubmissionOrdering ordering,
			@RequestParam(value = "pageOffset") int offset,
			@RequestParam(value = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		return submissionDao.fetchByChapterGroupAndSubGroup(
				chapterGroupId,
				subgroupId,
				user.getId(),
				ordering,
				PageParameters.of(offset, pageSize)
		);
	}

	/**
	 * Returns a specific submission
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to which the submissions belongs.
	 * @param chapterGroupId The ID of the chapter group the submission belongs to.
	 * @param submissionId   The ID of the submission.
	 * @param includeDeleted True if soft-deleted submissions should be included. False if not. This
	 *                       parameter is ignored if the logged in user is a student.
	 * @return The specific submission.
	 * @throws NoSuchEntityException When the submission does not exist.
	 */
	@ApiOperation("Get a particular submission. Will return a 404 if the submission is soft " +
			"deleted and 'include-deleted' is false.")
	@GetMapping("/{submissionId}")
	public SubmissionDetailsDto get(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId,
			@ApiParam("Also show soft deleted submissions")
			@RequestParam(value = "include-deleted", defaultValue = "false") boolean includeDeleted
	) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		// Set the flag to false if the user is just a student (effectively ignoring it)
		includeDeleted = includeDeleted
				&& securityService.hasPermission(user, chapterGroupId, "TA", "TEACHER");
		return NoSuchEntityException.checkNull(
				submissionDao.findByChapterGroupAndSubmissionId(
						chapterId, chapterGroupId, submissionId, user.getId(), includeDeleted),
				"No such submission");
	}

	/**
	 * Update a submission. When the logged in user is a teacher, it can set the "best-work" flag
	 * and soft-delete the submission.
	 * If the user is a student, and is the author of the submission, it can only soft-delete the
	 * submission.
	 *
	 * @param user             The logged in user, used to check vote status.
	 * @param chapterId        The ID of the chapter to which the submissions belongs.
	 * @param chapterGroupId   The ID of the chapter group the submission belongs to.
	 * @param submissionId     The ID of the submission to update.
	 * @param submissionUpdate The parameters used to update the submission with.
	 * @return The updated submission
	 * @throws NoSuchEntityException When the submission does not exist.
	 */
	@ApiOperation("Update the submission (set 'best work' and 'soft deleted')." +
			"If the user is a student, the student must be the author and the 'best work' " +
			"attribute will be ignored. If soft delete is set to true, the submission will still " +
			"be returned by this operation.")
	@PutMapping("/{submissionId}")
	public SubmissionDetailsDto put(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId,
			@RequestBody @Valid SubmissionUpdateDto submissionUpdate) {
		SubmissionDetailsDto submission = NoSuchEntityException.checkNull(
				this.submissionDao.findByChapterGroupAndSubmissionId(
						chapterId, chapterGroupId, submissionId, user.getId(), true),
				"No such submission");

		// 'best work' is to be ignored if the user is a student
		boolean mayEditOthersSubmission = this.securityService
				.hasPermission(user, chapterGroupId, "TEACHER");
		if (!mayEditOthersSubmission) {
			if (!user.getId().equals(submission.getUserId())) {
				throw new NoAccessException("Cannot modify others submission");
			}

			// if you may not edit others submissions, you are a student, so ignore changes in
			// the best work flag
			submissionUpdate.setBestWork(submission.getBestWork());
		}

		if (!submission.getBestWork() && submissionUpdate.getBestWork()) {
			// This work is now being marked as best work -> notify
			this.notificationDetailsDao.handleSubmissionBestWorkEvent(submission);
		}

		this.submissionDao.update(submissionId, submissionUpdate);
		submission.setBestWork(submissionUpdate.getBestWork());
		submission.setSoftDeleted(submissionUpdate.getSoftDeleted());

		return submission;
	}

	/**
	 * Vote for a submission. Increases the vote count if the user has not already voted before.
	 * Soft-deleted submissions can not be voted on.
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to which the submissions belongs.
	 * @param chapterGroupId The ID of the chapter group the submission belongs to.
	 * @param submissionId   The ID of the submission to vote for.
	 * @return The submission the user has voted for.
	 * @throws NoSuchEntityException When the submission does not exist or is soft-deleted.
	 */
	@ApiOperation("Cast a vote for the submission. Votes for soft deleted submissions will " +
			"return a 404, even if the user can see them.")
	@PutMapping("/{submissionId}/vote")
	public SubmissionDetailsDto vote(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId) {
		SubmissionDetailsDto submission = NoSuchEntityException.checkNull(
				submissionDao.findByChapterGroupAndSubmissionId(
						chapterId, chapterGroupId, submissionId, user.getId(), false),
				"No such submission");

		if (!submission.getUserHasVoted()) {
			voteDao.insertOrDoNothing(new Vote(submissionId, user.getId()));
			submission.setVotes(voteDao.countBySubmissionId(submissionId));
			submission.setUserHasVoted(true);
		}
		return submission;
	}

	/**
	 * Delete a vote for a submission. Decreases the vote count if the user has already voted
	 * before. Soft-deleted submissions can not alter vote state..
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to which the submissions belongs.
	 * @param chapterGroupId The ID of the chapter group the submission belongs to.
	 * @param submissionId   The ID of the submission to delete the vote for.
	 * @return The submission the user has removed it's vote for..
	 * @throws NoSuchEntityException When the submission does not exist or is soft-deleted.
	 */
	@ApiOperation("Delete this users vote for the submission." +
			"Votes for soft deleted submissions will return a 404, even if the user can see them.")
	@DeleteMapping("/{submissionId}/vote")
	public SubmissionDetailsDto unvote(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId) {
		SubmissionDetailsDto submission = NoSuchEntityException.checkNull(
				submissionDao.findByChapterGroupAndSubmissionId(
						chapterId, chapterGroupId, submissionId, user.getId(), false),
				"No such submission");

		if (!submission.getUser().getId().equals(user.getId()) && submission.getUserHasVoted()) {
			voteDao.delete(new Vote(submissionId, user.getId()));
			submission.setVotes(submission.getVotes() - 1);
			submission.setUserHasVoted(false);
		}
		return submission;
	}

	/**
	 * Retrives the file for a specific submission. Will throw an exception if the submission is
	 * soft-deleted and the user is not a TA in this chapter group or a teacher.
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to which the submissions belongs.
	 * @param chapterGroupId The ID of the chapter group the submission belongs to.
	 * @param submissionId   The ID of the submission for which to retrieve the file.
	 * @param response       The HTTP response where the file should be written to.
	 * @throws IOException           When the file could not be parsed.
	 * @throws NoSuchEntityException When the submission does not exist.
	 */
	@ApiOperation("Get the file of a particular submission." +
			"Will return 404 if the file is softdeleted and the user is not a TA or Teacher.")
	@GetMapping("/{submissionId}/file")
	public void getFile(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId, HttpServletResponse response) throws IOException {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		boolean canSeeSoftDeleted = this.securityService
				.hasPermission(user, chapterGroupId, "TA", "TEACHER");

		SubmissionFile file = NoSuchEntityException.checkNull(
				submissionFileDao.findBySubmissionIdAndChapterGroupId(
						submissionId, chapterGroupId, canSeeSoftDeleted),
				"No such submission");

		this.fileService.write(file.getData(), file.getMimeType(), response);
	}

	/**
	 * Alter the submission by applying a transformation on it, e.g. rotate or flip the image.
	 *
	 * @param chapterId      The ID of the chapter to which the submissions belongs.
	 * @param chapterGroupId The ID of the chaptergroup the submission belongs to.
	 * @param submissionId   The ID of the submission.
	 * @param transformation The transformation to apply.
	 * @throws IOException When the transformation can not be applied due to an error.
	 */
	@ApiOperation(value = "Alter the rotation of the submission",
			notes = "Also updates thumbnail and annotations")
	@PutMapping("/{submissionId}/file")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void transform(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId,
			@RequestParam ImageService.Transformation transformation,
			@AuthenticationPrincipal User user
	) throws IOException {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);

		final SubmissionDetailsDto submission = NoSuchEntityException.checkNull(
				this.submissionDao.findByChapterGroupAndSubmissionId(
						chapterId, chapterGroupId, submissionId, user.getId(), false),
				"No such submission"
		);

		if (!user.getId().equals(submission.getUserId())
				&& !user.getRole().equals(UserRole.TEACHER.toString())) {
			throw new NoAccessException(
					"Only teachers and the owner of the file may transform it."
			);
		}

		// Retrieve submission
		final SubmissionFile file = NoSuchEntityException.checkNull(
				submissionFileDao.findBySubmissionIdAndChapterGroupId(
						submissionId, chapterGroupId, false),
				"No such submission."
		);
		// Retrieve corresponding thumbnail
		final SubmissionThumbnail thumbnail = NoSuchEntityException.checkNull(
				submissionThumbnailDao.findBySubmissionIdAndChapterGroupId(
						submissionId, chapterGroupId, false),
				"No such thumbnail"
		);
		// Retrieve all annotations for the submission
		final List<Annotation> annotations = annotationDetailsDao.fetchBySubmissionId(submissionId);

		// Transform the image.
		final ImageFormat format = ImageFormat.ofMimeType(file.getMimeType());
		final byte[] transformedImage = imageService.transformImage(
				file.getData(),
				transformation,
				format.getFormat()
		);

		// Update our DTOs.
		file.setData(transformedImage);
		thumbnail.setData(imageService.createThumbnail(transformedImage));
		annotations.forEach(annotation -> {
			RotationState state = RotationState.ofState(
					annotation.getInvertX(),
					annotation.getInvertY(),
					annotation.getFlipXy()
			).transform(transformation);
			annotation.setInvertX(state.isInvertX());
			annotation.setInvertY(state.isInvertY());
			annotation.setFlipXy(state.isFlipXY());
		});

		// Update the database.
		submissionFileDao.update(file);
		submissionThumbnailDao.update(thumbnail);
		annotationDetailsDao.update(annotations);
	}

	/**
	 * Retrives the thumbnail file for a specific submission. Will throw an exception if the
	 * submission is soft-deleted and the user is not a TA in this chapter group or a teacher.
	 *
	 * @param user           The logged in user, used to check vote status.
	 * @param chapterId      The ID of the chapter to which the submissions belongs.
	 * @param chapterGroupId The ID of the chapter group the submission belongs to.
	 * @param submissionId   The ID of the submission for which to retrieve the thumbnail file.
	 * @param response       The HTTP response where the thumbnail file should be written to.
	 * @throws IOException           When the file could not be parsed.
	 * @throws NoSuchEntityException When the submission does not exist.
	 */
	@ApiOperation("Get the thumbnail file of a particular submission")
	@GetMapping("/{submissionId}/thumbnail")
	public void getFileThumbnail(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int submissionId, HttpServletResponse response) throws IOException {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		boolean canSeeSoftDeleted = this.securityService
				.hasPermission(user, chapterGroupId, "TA", "TEACHER");

		SubmissionThumbnail file = NoSuchEntityException.checkNull(
				submissionThumbnailDao.findBySubmissionIdAndChapterGroupId(
						submissionId, chapterGroupId, canSeeSoftDeleted),
				"No such submission");

		this.fileService.write(file.getData(), ImageService.THUMBNAIL_TYPE.getMimeType(), response);
	}

	/**
	 * Creates a submission
	 *
	 * @param user           The logged in user, submission will be created for this user.
	 * @param chapterId      The ID of the chapter this submission is for.
	 * @param chapterGroupId The ID of the chapter group this submission is for.
	 * @param taskId         the ID of the task this submission is for.
	 * @param file           The file to submit.
	 * @return The created submission metadata.
	 * @throws IOException               When the file could not be parsed properly.
	 * @throws NoSuchEntityException     When the task or chapter group could not be found for the
	 *                                   given parameters.
	 * @throws MalformedRequestException When the mimetype of the file was not correct.
	 */
	@ApiOperation("A 'normal' HTTP Multipart Form POST with the submission")
	@PostMapping
	public SubmissionDetailsDto post(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@NotNull @QueryParam("taskId") Integer taskId,
			@RequestBody MultipartFile file) throws IOException {

		// Chapter group and task check
		NoSuchEntityException.checkNull(
				taskDao.findByIdAndChapterId(taskId, chapterId),
				"No such task");
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group");

		String mimeType = this.imageService.detectImageMime(file);
		byte[] resizedImage = this.imageService.resizeImage(
				file.getBytes(),
				ImageFormat.ofMimeType(mimeType),
				ImageService.MAX_SUBMISSION_WIDTH,
				ImageService.MAX_SUBMISSION_HEIGHT,
				false);

		// Create submission
		Submission submission = new Submission();
		submission.setBestWork(false);
		submission.setChapterGroupId(chapterGroupId);
		submission.setTaskId(taskId);
		submission.setUserId(user.getId());
		int submissionId = this.submissionDao.insertAndGetId(submission);

		// Create 'vote' (people immediately vote for their own stuff)
		Vote vote = new Vote();
		vote.setSubmissionId(submissionId);
		vote.setUserId(user.getId());
		this.voteDao.insertOrDoNothing(vote);

		// Insert file
		SubmissionFile submissionFile = new SubmissionFile();
		submissionFile.setMimeType(mimeType);
		submissionFile.setData(resizedImage);
		submissionFile.setSubmissionId(submissionId);
		this.submissionFileDao.insert(submissionFile);

		// Create and insert thumbnail
		byte[] thumb = imageService.createThumbnail(resizedImage);
		SubmissionThumbnail submissionThumbnail = new SubmissionThumbnail();
		submissionThumbnail.setData(thumb);
		submissionThumbnail.setSubmissionId(submissionId);
		this.submissionThumbnailDao.insert(submissionThumbnail);

		return this.submissionDao.findByChapterGroupAndSubmissionId(
				chapterId, chapterGroupId, submissionId, user.getId(), false);
	}
}
