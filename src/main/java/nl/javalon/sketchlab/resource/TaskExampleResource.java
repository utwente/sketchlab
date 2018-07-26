package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.ExampleSubmissionDetailsDao;
import nl.javalon.sketchlab.dao.ExampleSubmissionFileDetailsDao;
import nl.javalon.sketchlab.dao.ExampleSubmissionThumbnailDetailsDao;
import nl.javalon.sketchlab.dao.TaskDetailsDao;
import nl.javalon.sketchlab.dto.task.ExampleSubmissionDetailsDto;
import nl.javalon.sketchlab.dto.task.ExampleSubmissionUpdateDto;
import nl.javalon.sketchlab.entity.tables.pojos.ExampleSubmission;
import nl.javalon.sketchlab.entity.tables.pojos.ExampleSubmissionFile;
import nl.javalon.sketchlab.entity.tables.pojos.ExampleSubmissionThumbnail;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.service.FileService;
import nl.javalon.sketchlab.service.ImageService;
import nl.javalon.sketchlab.service.ImageService.ImageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.List;

/**
 * REST endpoint for work examples on tasks.
 *
 * @author Lukas Miedema
 */
@SketchlabResource
@RequestMapping(ApiConfig.TASK_EXAMPLE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Manages example submissions for a task")
public class TaskExampleResource {

	private final ExampleSubmissionDetailsDao exampleSubmissionDao;
	private final ExampleSubmissionFileDetailsDao exampleSubmissionFileDao;
	private final ExampleSubmissionThumbnailDetailsDao exampleSubmissionThumbnailDao;
	private final TaskDetailsDao taskDao;
	private final ImageService imageService;
	private final FileService fileService;

	/**
	 * Retrieves a list of all work examples for a specific task.
	 *
	 * @param chapterId The ID of the chapter to which the task belongs to.
	 * @param taskId    The ID of the task.
	 * @return A list of all work examples for the given task.
	 */
	@ApiOperation("Get all example submissions")
	@GetMapping
	public List<ExampleSubmissionDetailsDto> getAll(
			@PathVariable int chapterId,
			@PathVariable int taskId) {
		return exampleSubmissionDao.fetchDetailedByTask(taskId, chapterId);
	}

	/**
	 * Modifies an example submission.
	 *
	 * @param chapterId           The ID of the chapter the example submission belongs to.
	 * @param taskId              The ID of the task the chapter belongs to.
	 * @param exampleSubmissionId The ID of the example submission.
	 * @param updateDto           The data to update the example submission with.
	 * @return The updated example submission.
	 */
	@ApiOperation("Modify an example submission")
	@PutMapping("/{exampleSubmissionId}")
	public ExampleSubmissionDetailsDto put(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int exampleSubmissionId,
			@Valid @RequestBody ExampleSubmissionUpdateDto updateDto) {
		ExampleSubmissionDetailsDto original = NoSuchEntityException.checkNull(
				exampleSubmissionDao.findByIdAndTaskIdAndChapterId(
						exampleSubmissionId, taskId, chapterId),
				"No such example submission"
		);
		original.setComment(updateDto.getComment());
		exampleSubmissionDao.update(original);
		return original;
	}

	/**
	 * Deletes a work example.
	 *
	 * @param chapterId           The ID of the chapter the example submission belongs to.
	 * @param taskId              The ID of the task the example submission belongs to.
	 * @param exampleSubmissionId The ID of the sample submission.
	 */
	@ApiOperation("Delete an example submission")
	@DeleteMapping("/{exampleSubmissionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int exampleSubmissionId) {
		exampleSubmissionDao.deleteByIdAndTaskId(exampleSubmissionId, taskId, chapterId);
	}

	/**
	 * Retrieves the file for a example submission.
	 *
	 * @param chapterId           The ID of the chapter the example submission belongs to.
	 * @param taskId              The ID of the task the example submission belongs to.
	 * @param exampleSubmissionId The ID of the example submission.
	 * @param response            The HTTP response to write to.
	 * @throws IOException When the HTTP response can not be written to.
	 */
	@ApiOperation("Get the file of the example submission.")
	@GetMapping("/{exampleSubmissionId}/file")
	public void getFile(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int exampleSubmissionId,
			HttpServletResponse response) throws IOException {
		ExampleSubmissionFile file = NoSuchEntityException.checkNull(
				exampleSubmissionFileDao.findByTaskAndExampleSubmissionId(
						exampleSubmissionId, chapterId, taskId),
				"No such file"
		);
		fileService.write(file.getData(), file.getMimeType(), response);
	}

	/**
	 * Alters a submission by applying a certain transformation to it.
	 *
	 * @param chapterId           The ID of the chapter the example submission belongs to.
	 * @param taskId              The ID of the task the example submission belongs to.
	 * @param exampleSubmissionId The ID of the example submission.
	 * @param transformation      The transformation to apply.
	 * @throws IOException When the transformation can not be applied for whatever reason.
	 */
	@ApiOperation(value = "Alter the image by rotating or flipping it. Also updates thumbnail",
			notes = "This allows images to be displayed correctly when uploaded with a wrong" +
					"orientation.")
	@PutMapping("/{exampleSubmissionId}/file")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void rotate(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int exampleSubmissionId,
			@RequestParam ImageService.Transformation transformation)
			throws IOException {
		final ExampleSubmissionFile file = NoSuchEntityException.checkNull(
				exampleSubmissionFileDao.findByTaskAndExampleSubmissionId(
						exampleSubmissionId, chapterId, taskId),
				"No such file");
		final ExampleSubmissionThumbnail thumbnail = NoSuchEntityException.checkNull(
				exampleSubmissionThumbnailDao.findByTaskAndExampleSubmissionId(
						exampleSubmissionId, chapterId, taskId),
				"No such thumbnail"
		);

		final ImageFormat format = ImageFormat.ofMimeType(file.getMimeType());
		final byte[] transformedImage = imageService.transformImage(
				file.getData(),
				transformation,
				format.getFormat()
		);

		file.setData(transformedImage);
		thumbnail.setData(imageService.createThumbnail(transformedImage));
		exampleSubmissionFileDao.update(file);
		exampleSubmissionThumbnailDao.update(thumbnail);
	}

	/**
	 * Retrieves the thumbnail for a example submission.
	 *
	 * @param chapterId           The ID of the chapter the example submission belongs to.
	 * @param taskId              The ID of the task the example submission belongs to.
	 * @param exampleSubmissionId The ID of the example submission.
	 * @param response            The HTTP response to write to.
	 * @throws IOException When the HTTP response can not be written to.
	 */
	@ApiOperation("Get the thumbnail of the example submission.")
	@GetMapping("/{exampleSubmissionId}/thumbnail")
	public void getThumbnail(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int exampleSubmissionId,
			HttpServletResponse response) throws IOException {
		ExampleSubmissionThumbnail file = NoSuchEntityException.checkNull(
				exampleSubmissionThumbnailDao.findByTaskAndExampleSubmissionId(
						exampleSubmissionId, chapterId, taskId),
				"No such thumbnail"
		);
		fileService.write(file.getData(), ImageService.THUMBNAIL_TYPE.getMimeType(), response);
	}

	/**
	 * Creates a new examplesubmission.
	 *
	 * @param user      The user ID that submits this work example
	 * @param chapterId The ID of the chapter this work example belongs to.
	 * @param taskId    The ID of the task this chapter belongs to.
	 * @param comment   The comment along with the work example.
	 * @param file      The file for the example submission.
	 * @return The created example submission.
	 * @throws IOException When either the image can not be processed correctly.
	 */
	@ApiOperation("A 'normal' HTTP Multipart Form POST with the example submission")
	@PostMapping
	public ExampleSubmissionDetailsDto post(
			@AuthenticationPrincipal User user,
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@QueryParam("comment") String comment,
			@RequestBody MultipartFile file) throws IOException {
		// Task check
		NoSuchEntityException.checkNull(
				taskDao.findByIdAndChapterId(taskId, chapterId),
				"No such task"
		);

		String mimeType = this.imageService.detectImageMime(file);

		byte[] resizedImage = imageService.resizeImage(
				file.getBytes(),
				ImageFormat.ofMimeType(mimeType),
				ImageService.MAX_SUBMISSION_WIDTH,
				ImageService.MAX_SUBMISSION_HEIGHT,
				false);

		// Create example submission
		ExampleSubmission submission = new ExampleSubmission();

		comment = comment != null ? comment : "";
		submission.setComment(comment);
		submission.setTaskId(taskId);
		submission.setUserId(user.getId());
		int submissionId = this.exampleSubmissionDao.insertAndGetId(submission);

		// Insert file
		ExampleSubmissionFile submissionFile = new ExampleSubmissionFile();
		submissionFile.setMimeType(mimeType);
		submissionFile.setData(resizedImage);
		submissionFile.setExampleSubmissionId(submissionId);
		this.exampleSubmissionFileDao.insert(submissionFile);

		// Create and insert thumbnail
		byte[] thumb = imageService.createThumbnail(resizedImage);
		ExampleSubmissionThumbnail submissionThumbnail = new ExampleSubmissionThumbnail();
		submissionThumbnail.setData(thumb);
		submissionThumbnail.setExampleSubmissionId(submissionId);
		this.exampleSubmissionThumbnailDao.insert(submissionThumbnail);

		return this.exampleSubmissionDao
				.findByIdAndTaskIdAndChapterId(submissionId, chapterId, taskId);
	}
}
