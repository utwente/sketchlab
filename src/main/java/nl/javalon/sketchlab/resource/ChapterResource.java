package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.ChapterDetailsDao;
import nl.javalon.sketchlab.dao.TaskDetailsDao;
import nl.javalon.sketchlab.dto.chapter.ChapterCreateDto;
import nl.javalon.sketchlab.dto.chapter.ChapterGroupDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.Chapter;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Handles management of chapters. This is completely teacher-only, as teaching assistants operate
 * on the chapter-group level.
 *
 * @author Lukas Miedema
 */
@SketchlabResource
@RequestMapping(ApiConfig.CHAPTER)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Management resource for chapters (teacher only)")
public class ChapterResource {
	private final ChapterDetailsDao chapterDao;
	private final TaskDetailsDao taskDao;

	/**
	 * Retrieves all chapters in the system, only fetches top level objects, no underlying children.
	 *
	 * @return A List of all chapters in the system.
	 */
	@ApiOperation("Get all chapters, without any details")
	@GetMapping
	public List<Chapter> getAll() {
		return chapterDao.findAll();
	}

	/**
	 * Retrieves a specific chapter in the system. Only fetches top level objects, no underlying
	 * children.
	 *
	 * @param chapterId The ID of the chapter.
	 * @return The corresponding chapter.
	 * @throws NoSuchEntityException When the chapter does not exist.
	 */
	@ApiOperation("Get one specific chapter")
	@GetMapping("/{chapterId}")
	public Chapter get(@PathVariable int chapterId) {
		return NoSuchEntityException.checkNull(
				chapterDao.findById(chapterId),
				"No such chapter");
	}

	/**
	 * Retrieves all chapters in the system, with corresponding tasks, sorted by track.
	 *
	 * @return A List of all chapters in the system, with corresponding tasks.
	 */
	@ApiOperation("Get all chapters with corresponding tasks, sorted descending (newest first)")
	@GetMapping("/tasks")
	public List<ChapterGroupDetailsDto> getTasks() {
		return chapterDao.fetchAllWithTasks();
	}

	/**
	 * Creates a new chapter and inserts it into the database.
	 *
	 * @param chapterDto The parameters for the Chapter to create.
	 * @return The created Chapter, with a newly generated ID.
	 */
	@ApiOperation("Create a new chapter. The new chapter is returned with new, generated id.")
	@PostMapping
	public Chapter post(@RequestBody @Valid ChapterCreateDto chapterDto) {
		Chapter chapter = new Chapter();
		chapter.setLabel(chapterDto.getLabel());
		chapter.setId(chapterDao.insertAndGetId(chapter));
		return chapter;
	}

	/**
	 * Creates a new chapter by copying an existing chapter, with all tasks.
	 *
	 * @param chapterDto The parameters for the Chapter to create.
	 * @return The created Chapter, with a newly generated ID.
	 */
	@ApiOperation("Copy a chapter and its tasks (but not chapter groups). The new chapter is returned with new, generated id.")
	@PostMapping("/{chapterId}/copy")
	public Chapter copy(@PathVariable int chapterId, @RequestBody @Valid ChapterCreateDto chapterDto) {
		Chapter original = get(chapterId);
		Chapter copy = post(chapterDto);
		this.taskDao.copyTasks(original.getId(), copy.getId());
		return copy;
	}

	/**
	 * Edits a chapter and returns the updated Chapter.
	 *
	 * @param chapterId  The ID of the chapter to update.
	 * @param chapterDto The parameters for the chapter to update.
	 * @return The updated chapter.
	 * @throws NoSuchEntityException When the chapter does not exist.
	 */
	@ApiOperation("Edit a chapter")
	@PutMapping("/{chapterId}")
	public Chapter put(
			@PathVariable int chapterId, @RequestBody @Valid ChapterCreateDto chapterDto) {
		Chapter chapter = NoSuchEntityException.checkNull(
				chapterDao.findById(chapterId),
				"No such chapter."
		);

		chapter.setLabel(chapterDto.getLabel());
		chapterDao.update(chapter);
		return chapter;
	}

	/**
	 * Deletes a chapter by the given chapter ID.
	 *
	 * @param chapterId The ID of the chapter to delete.
	 */
	@ApiOperation("Delete a chapter and all related data. This is destructive.")
	@DeleteMapping("/{chapterId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable int chapterId) {
		chapterDao.deleteById(chapterId);
	}
}
