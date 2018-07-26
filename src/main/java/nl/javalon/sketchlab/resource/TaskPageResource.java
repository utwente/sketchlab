package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.TaskDetailsDao;
import nl.javalon.sketchlab.dao.TaskPageDetailsDao;
import nl.javalon.sketchlab.dto.task.TaskPageCreateDto;
import nl.javalon.sketchlab.dto.task.TaskPageSwapSlotsDto;
import nl.javalon.sketchlab.entity.tables.pojos.Task;
import nl.javalon.sketchlab.entity.tables.pojos.TaskPage;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST End point for Task Pages
 *
 * @author Jelle Stege
 */
@SketchlabResource
@RequestMapping(ApiConfig.TASK_PAGE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Management for task pages")
public class TaskPageResource {
	private final TaskPageDetailsDao taskPageDao;
	private final TaskDetailsDao taskDao;

	/**
	 * Retrieves all task pages for a given task.
	 *
	 * @param chapterId The ID of the chapter to which the task belongs.
	 * @param taskId    The ID of the task to fetch pages for.
	 * @return The task pages for the given task.
	 */
	@ApiOperation("Get all task pages")
	@GetMapping
	public List<TaskPage> getAll(@PathVariable int chapterId, @PathVariable int taskId) {
		return taskPageDao.fetchByTaskIdAndChapterId(taskId, chapterId);
	}

	/**
	 * Retrieves a single task page for a given task.
	 *
	 * @param chapterId  The ID of the chapter to which the task belongs.
	 * @param taskId     The ID of the task to fetch a page for.
	 * @param taskPageId The ID of the task page.
	 * @return The task page for the given task page ID.
	 */
	@ApiOperation("Retrieve a single task page by id")
	@GetMapping("/{taskPageId}")
	public TaskPage get(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int taskPageId) {
		return NoSuchEntityException.checkNull(
				taskPageDao.findByIdAndTaskIdAndChapterId(taskPageId, taskId, chapterId),
				"No such task page");
	}

	/**
	 * Creates a new task page. Also updates other task pages if slots overlap or do not match up
	 * somehow.
	 *
	 * @param author            The author of the page, which would be the currently logged in user.
	 * @param chapterId         The ID of the chapter to which the task belongs.
	 * @param taskId            The task for which to create a task page.
	 * @param taskPageCreateDto The parameters for the to be generated task page.
	 * @return All task pages for the given task.
	 */
	@ApiOperation(
			value = "Add a task page to a task",
			notes = "The user becomes the author of the task.")
	@PostMapping
	public List<TaskPage> post(
			@AuthenticationPrincipal User author,
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@Valid @RequestBody TaskPageCreateDto taskPageCreateDto) {
		// Check chapter id
		Task task = this.taskDao.fetchOneById(taskId);
		if (task == null || task.getChapterId() != chapterId) {
			throw new NoSuchEntityException("No such task");
		}

		List<TaskPage> otherTaskPages = taskPageDao.fetchByTaskIdAndChapterId(taskId, chapterId);

		final int newSlot = taskPageCreateDto.getSlot() == null || taskPageCreateDto.getSlot() < 1
				? otherTaskPages.stream().mapToInt(TaskPage::getSlot).max().orElse(0) + 1
				: taskPageCreateDto.getSlot();

		TaskPage taskPage = new TaskPage();
		taskPage.setTaskId(taskId);
		taskPage.setTitle(taskPageCreateDto.getTitle());
		taskPage.setAuthorId(author.getId());
		taskPage.setSlot(newSlot);

		String text = taskPageCreateDto.getText();
		if (text != null) {
			taskPage.setText(text);
		}

		String videoUrl = taskPageCreateDto.getVideoUrl();
		if (videoUrl != null) {
			taskPage.setVideoUrl(videoUrl);
		}

		// Sort new task page into old task pages. 
		List<TaskPage> resultTaskPages = recalculateSlots(otherTaskPages, taskPage);

		// Update old task pages to make room for the new one.
		taskPageDao.update(otherTaskPages);

		// Insert new task page at right position. 
		final int newId = taskPageDao.insertAndGetId(taskPage);
		taskPage.setId(newId);

		return resultTaskPages;
	}

	/**
	 * Updates a given task page and corresponding other task pages if the slots do no longer line
	 * up.
	 *
	 * @param author            The new author of the task page, which would be the currently logged
	 *                          in user.
	 * @param chapterId         The ID of the chapter to which the task belongs.
	 * @param taskId            The ID of the task to which the task page belongs.
	 * @param taskPageId        The ID of the task page to update.
	 * @param taskPageCreateDto The parameters for the updated task page.
	 * @return All task pages for the given task.
	 */
	@ApiOperation(value = "Modifies a task page", notes = "Task page must exist beforehand")
	@PutMapping("/{taskPageId}")
	public List<TaskPage> put(
			@AuthenticationPrincipal User author,
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int taskPageId,
			@Valid @RequestBody TaskPageCreateDto taskPageCreateDto) {
		final TaskPage taskPage = NoSuchEntityException.checkNull(
				this.taskPageDao.findByIdAndTaskIdAndChapterId(taskPageId, taskId, chapterId),
				"No such task page."
		);
		final List<TaskPage> otherTaskPages = this.taskPageDao
				.fetchByTaskIdAndChapterId(taskId, chapterId)
				.stream()
				.filter(tp -> !tp.getId().equals(taskPage.getId()))
				.collect(Collectors.toList());
		final int newSlot = taskPageCreateDto.getSlot() == null || taskPageCreateDto.getSlot() < 1
				? otherTaskPages.stream().mapToInt(TaskPage::getSlot).max().orElse(0) + 1
				: taskPageCreateDto.getSlot();

		taskPage.setTitle(taskPageCreateDto.getTitle());
		taskPage.setAuthorId(author.getId());
		taskPage.setText(taskPageCreateDto.getText());
		taskPage.setVideoUrl(taskPageCreateDto.getVideoUrl());
		taskPage.setSlot(newSlot);

		// Sort new task page into old task pages. 
		// Split original task pages up in two lists of task pages, the ones with smaller slots and
		// the ones with (equal to or) larger slots. Then put the current task page in between.
		List<TaskPage> resultTaskPages = recalculateSlots(otherTaskPages, taskPage);

		taskPageDao.update(resultTaskPages);

		return resultTaskPages;
	}

	/**
	 * Swaps the slots of two given task pages.
	 *
	 * @param chapterId The chapter ID of the task pages.
	 * @param taskId    The task ID of the task pages.
	 * @param swapDto   The IDs of the task pages to swap.
	 * @return All task pages for the given task.
	 */
	@ApiOperation(value = "Swaps the slots of two given task pages")
	@PutMapping("/swap")
	public List<TaskPage> swapSlots(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@Valid @RequestBody TaskPageSwapSlotsDto swapDto) {
		final TaskPage firstTaskPage = NoSuchEntityException.checkNull(
				taskPageDao.findByIdAndTaskIdAndChapterId(
						swapDto.getFirstTaskPage(), taskId, chapterId),
				"First given task page does not exist"
		);
		final TaskPage secondTaskPage = NoSuchEntityException.checkNull(
				taskPageDao.findByIdAndTaskIdAndChapterId(
						swapDto.getSecondTaskPage(), taskId, chapterId),
				"First given task page does not exist"
		);

		int oldSlot = firstTaskPage.getSlot();
		firstTaskPage.setSlot(secondTaskPage.getSlot());
		secondTaskPage.setSlot(oldSlot);
		taskPageDao.update(firstTaskPage, secondTaskPage);
		return taskPageDao.fetchByTaskIdAndChapterId(taskId, chapterId);
	}

	/**
	 * Deletes a task page and all associated data.
	 *
	 * @param chapterId  The ID of the chapter to which the task page belongs.
	 * @param taskId     The ID of the task to which the task page belongs.
	 * @param taskPageId The ID of the task page to delete.
	 */
	@ApiOperation("Delete a task page and all associated data")
	@DeleteMapping("/{taskPageId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@PathVariable int chapterId,
			@PathVariable int taskId,
			@PathVariable int taskPageId) {
		// Check if it exists.
		this.get(chapterId, taskId, taskPageId);
		taskPageDao.deleteById(taskPageId);

		List<TaskPage> taskPages = taskPageDao.fetchByTaskIdAndChapterId(taskId, chapterId);
		recalculateSlots(taskPages, null);
	}

	/**
	 * Updates given task pages and calculates their correct slots. Note that this method does not
	 * make the changes persistent in the database.
	 *
	 * @param taskPages The task pages in which to sort the other given taskPage. Assumes the other
	 *                  task pages is not present in this list.
	 * @param taskPage  The task page to sort into the other task pages.
	 * @return A sorted List of task pages, with their recalculated slots.
	 */
	private static List<TaskPage> recalculateSlots(List<TaskPage> taskPages, TaskPage taskPage) {
		List<TaskPage> resultTaskPages = new ArrayList<>(taskPages.size() + 1);
		int previousSlot = 0;
		int currentSlot = 1;
		for (TaskPage tp : taskPages) {
			if (taskPage != null
					&& previousSlot < taskPage.getSlot() && taskPage.getSlot() <= tp.getSlot()) {
				resultTaskPages.add(taskPage);
				taskPage.setSlot(currentSlot++);
			}
			previousSlot = tp.getSlot();
			resultTaskPages.add(tp);
			tp.setSlot(currentSlot++);
		}

		if (taskPage != null && previousSlot < taskPage.getSlot()) {
			resultTaskPages.add(taskPage);
			taskPage.setSlot(currentSlot);
		}

		return resultTaskPages;
	}
}
