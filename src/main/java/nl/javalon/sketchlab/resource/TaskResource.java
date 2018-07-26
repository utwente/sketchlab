package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.NotificationDetailsDao;
import nl.javalon.sketchlab.dao.TaskDetailsDao;
import nl.javalon.sketchlab.dto.task.TaskCreateDto;
import nl.javalon.sketchlab.entity.tables.pojos.Task;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Everything tasks. Just like {@link ChapterResource}, the task resource is completely
 * teacher-only. Students see tasks via their chapter groups and teaching assistants are assigned based on
 * chapter groups.
 *
 * @author Lukas Miedema
 */
@SketchlabResource
@RequestMapping(ApiConfig.TASK)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Management for tasks (teacher only)")
public class TaskResource {
	private final TaskDetailsDao taskDao;
	private final NotificationDetailsDao notificationDetailsDao;

	@ApiOperation("Get all tasks")
	@GetMapping
	public List<Task> getAll(@PathVariable int chapterId) {
		return taskDao.fetchByChapterId(chapterId);
	}

	@ApiOperation("Returns the next tasks within the same track.")
	@GetMapping("/{taskId}/next-tasks")
	public List<Task> getRecommended(@PathVariable int chapterId, @PathVariable int taskId) {
		return taskDao.fetchNextTasks(taskId, chapterId);
	}

	@ApiOperation("Retrieve a single task by id")
	@GetMapping("/{taskId}")
	public Task get(@PathVariable int taskId, @PathVariable int chapterId) {
		return NoSuchEntityException.checkNull(
				taskDao.findByIdAndChapterId(taskId, chapterId),
				"No such task"
		);
	}

	@ApiOperation(
			value = "Add a task to a chapter",
			notes = "The slot id must not be in use for the track. The user becomes the author " +
					"of the task.")
	@PostMapping
	public Task post(
			@AuthenticationPrincipal User author,
			@PathVariable int chapterId,
			@Valid @RequestBody TaskCreateDto taskDto) {
		Task task = new Task();
		task.setName(taskDto.getName());
		task.setTrack(taskDto.getTrack().name());
		task.setSlot(taskDto.getSlot());

		task.setAuthorId(author.getId());
		task.setChapterId(chapterId);

		task.setId(taskDao.insertAndGetId(task));

		// Send creation notifications
		notificationDetailsDao.handleTaskCreationEvent(task);
		return task;
	}

	@ApiOperation(value = "Modify a task", notes = "The task must already exist.")
	@PutMapping("/{taskId}")
	public Task put(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@Valid @RequestBody TaskCreateDto taskDto) {
		Task task = NoSuchEntityException.checkNull(
				this.taskDao.findByIdAndChapterId(taskId, chapterId),
				"No such task"
		);
		task.setName(taskDto.getName());
		task.setTrack(taskDto.getTrack().name());
		task.setSlot(taskDto.getSlot());

		taskDao.update(task);
		return task;
	}

	@ApiOperation("Delete a task and all associated data")
	@DeleteMapping("/{taskId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable int taskId, @PathVariable int chapterId) {
		this.get(taskId, chapterId);
		taskDao.deleteById(taskId);
	}
}
