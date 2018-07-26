package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.TaskPageDetailsDao;
import nl.javalon.sketchlab.dao.TaskPageImageDetailsDao;
import nl.javalon.sketchlab.dto.task.TaskPageImageDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.TaskPageImage;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import nl.javalon.sketchlab.service.ImageService;
import nl.javalon.sketchlab.service.ImageService.ImageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * REST End point for Task Page Images.
 *
 * @author Lukas Miedema
 */
@SketchlabResource
@RequestMapping(ApiConfig.TASK_PAGE_IMAGE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Task page images")
public class TaskPageImageResource {
	private final TaskPageDetailsDao taskPageDao;
	private final TaskPageImageDetailsDao taskPageImageDao;
	private final ImageService imageService;

	/**
	 * Retrieves a list of all images for the given task page.
	 *
	 * @param chapterId  The ID of the chapter for the given task page.
	 * @param taskId     The ID of the task for the given task page.
	 * @param taskPageId The ID of the task page.
	 * @return A list of all images for the given parameters.
	 */
	@ApiOperation("List all images for the task page")
	@GetMapping
	public List<TaskPageImageDetailsDto> getAll(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int taskPageId) {
		return taskPageImageDao.fetchBySuperKey(chapterId, taskId, taskPageId);
	}

	/**
	 * Retrieves a specific image for the given parameters.
	 *
	 * @param chapterId       The ID of the chapter the image belongs to.
	 * @param taskId          The ID of the task the image belongs to.
	 * @param taskPageId      The ID of the task page the image belongs to.
	 * @param taskPageImageId The ID of the image.
	 * @param response        The HTTP response to write to.
	 * @throws IOException When the HTTP response can not be written to.
	 */
	@ApiOperation("Retrieve the image. The image is sent with the right mime type for immediate " +
			"viewing in the browser")
	@GetMapping("/{taskPageImageId}")
	public void get(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int taskPageId,
			@PathVariable int taskPageImageId,
			HttpServletResponse response) throws IOException {

		TaskPageImage image = NoSuchEntityException.checkNull(
				taskPageImageDao.findBySuperKey(chapterId, taskId, taskPageId, taskPageImageId),
				"No such image");
		response.setContentType(image.getMimeType());
		response.getOutputStream().write(image.getData());
	}

	/**
	 * Stores a new image for a task page. Only processes JPG and PNG images.
	 *
	 * @param chapterId  The ID of the chapter the image belongs to.
	 * @param taskId     The ID of the task the chapter belongs to.
	 * @param taskPageId The ID of the task page the image belongs to.
	 * @param file       The image to store.
	 * @return The created metadata for this image.
	 * @throws IOException When the image can not be processed.
	 */
	@ApiOperation(value = "Upload a new image", notes = "image/jp(e)g and image/png are supported")
	@PostMapping
	public TaskPageImageDetailsDto post(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int taskPageId,
			@RequestBody MultipartFile file) throws IOException {

		// Check task page
		NoSuchEntityException.checkNull(
				taskPageDao.findByIdAndTaskIdAndChapterId(taskPageId, taskId, chapterId),
				"No such task page");

		String mimeType = this.imageService.detectImageMime(file);

		byte[] resizedImage = imageService.resizeImage(
				file.getBytes(),
				ImageFormat.ofMimeType(mimeType),
				ImageService.MAX_SUBMISSION_WIDTH,
				ImageService.MAX_SUBMISSION_HEIGHT,
				false);
		TaskPageImage image = new TaskPageImage(null, taskPageId, mimeType, resizedImage);
		int id = this.taskPageImageDao.insertAndGetId(image);

		// Create dto
		TaskPageImageDetailsDto dto = new TaskPageImageDetailsDto();
		dto.setId(id);
		dto.setMimeType(mimeType);
		return dto;
	}

	/**
	 * Modifies a stored image by applying a transformation on it.
	 *
	 * @param chapterId       The ID of the chapter the image belongs to.
	 * @param taskId          The ID of the task the image belongs to.
	 * @param taskPageId      The ID of the task page the image belongs to.
	 * @param taskPageImageId The ID of the task page image.
	 * @param transformation  The transformation to apply.
	 * @throws IOException When the transformation can not be applied.
	 */
	@ApiOperation(value = "Alter the image by rotating or flipping it.",
			notes = "this allows images to be displayed correctly.")
	@PutMapping("/{taskPageImageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void rotate(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int taskPageId,
			@PathVariable int taskPageImageId,
			@RequestParam ImageService.Transformation transformation) throws IOException {
		TaskPageImage image = NoSuchEntityException.checkNull(
				taskPageImageDao.findBySuperKey(chapterId, taskId, taskPageId, taskPageImageId),
				"No such image");

		ImageFormat format = ImageFormat.ofMimeType(image.getMimeType());

		byte[] transformedImage = imageService
				.transformImage(image.getData(), transformation, format.getFormat());
		image.setData(transformedImage);
		taskPageImageDao.update(image);
	}

	/**
	 * Deletes an image.
	 *
	 * @param chapterId       The ID of the chapter the image belongs to.
	 * @param taskId          The ID of the task the image belongs to.
	 * @param taskPageId      The ID of the task page the image belongs to.
	 * @param taskPageImageId The ID of the image.
	 */
	@ApiOperation("Delete the image")
	@DeleteMapping("/{taskPageImageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int taskPageId,
			@PathVariable int taskPageImageId) {
		TaskPageImage image = NoSuchEntityException.checkNull(
				taskPageImageDao.findBySuperKey(chapterId, taskId, taskPageId, taskPageImageId),
				"No such image");
		taskPageImageDao.delete(image);
	}
}
