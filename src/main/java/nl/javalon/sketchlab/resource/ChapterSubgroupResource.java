package nl.javalon.sketchlab.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import nl.javalon.sketchlab.config.ApiConfig;
import nl.javalon.sketchlab.dao.ChapterGroupDetailsDao;
import nl.javalon.sketchlab.dao.ChapterSubgroupDetailsDao;
import nl.javalon.sketchlab.dto.chapter.ChapterSubgroupCreateDto;
import nl.javalon.sketchlab.dto.chapter.ChapterSubgroupDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterSubgroup;
import nl.javalon.sketchlab.entity.tables.pojos.User;
import nl.javalon.sketchlab.exception.EntityExistsException;
import nl.javalon.sketchlab.exception.NoSuchEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Handles management of chapter subgroups.
 *
 * @author Melcher Stikkelorum
 */
@SketchlabResource
@RequestMapping(ApiConfig.CHAPTER_SUBGROUP)
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Api(description = "Management resource for chapter subgroups")
public class ChapterSubgroupResource {
	private final ChapterSubgroupDetailsDao chapterSubgroupDao;
	private final ChapterGroupDetailsDao chapterGroupDao;

	/**
	 * Returns a list of all subgroups for the given chapter and chapter group the logged in user
	 * is enrolled to.
	 *
	 * @param chapterId      The ID of the chapter to which the subgroups belong to.
	 * @param chapterGroupId The ID of the chapter group the subgroups belong to.
	 * @param user           The user for which to fetch subgroups.
	 * @return A list of all subgroups belonging to the given parameters.
	 */
	@ApiOperation("Get all subgroups the current user is enrolled in")
	@GetMapping("/me")
	public List<ChapterSubgroupDetailsDto> getMe(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@AuthenticationPrincipal User user) {
		return this.getForUser(chapterId, chapterGroupId, user.getId());
	}

	/**
	 * Returns a list of all subgroups for the given chapter and chapter group to which the given 
	 * user is enrolled to.
	 *
	 * @param chapterId      The ID of the chapter to which the subgroups belong to.
	 * @param chapterGroupId The ID of the chapter group the subgroups belong to.
	 * @param userId           The user for which to fetch subgroups.
	 * @return A list of all subgroups belonging to the given parameters.
	 */
	@ApiOperation("Get all subgroups the current user is enrolled in")
	@GetMapping("/user/{userId}")
	public List<ChapterSubgroupDetailsDto> getForUser(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable UUID userId) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group"
		);
		return chapterSubgroupDao.fetchByUserAndChapterGroup(userId, chapterGroupId);
	}
	
	/**
	 * Returns a list of all subgroups for the given chapter and chapter group.
	 *
	 * @param chapterId      The ID of the chapter to which the subgroups belong to.
	 * @param chapterGroupId The ID of the chapter group the subgroups belong to.
	 * @return A list of all subgroups belonging to the given parameters.
	 */
	@ApiOperation("Get all subgroups for a given chapter group")
	@GetMapping
	public List<ChapterSubgroupDetailsDto> getAll(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group."
		);
		
		return chapterSubgroupDao.fetchByChapterGroupAndChapter(chapterGroupId, chapterId);
	}

	/**
	 * Retrieves a specific subgroup.
	 *
	 * @param chapterId      The ID of the chapter the subgroup belongs to.
	 * @param chapterGroupId The ID of the chapter group the subgroup belongs to.
	 * @param subgroupId     The ID of the subgroup to retrieve.
	 * @return The subgroup, as a {@link ChapterSubgroup}.
	 * @throws NoSuchEntityException When there is no such subgroup.
	 */
	@ApiOperation("Get a specific subgroup")
	@GetMapping("/{subgroupId}")
	public ChapterSubgroupDetailsDto get(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int subgroupId) {
		return NoSuchEntityException.checkNull(
				chapterSubgroupDao.findByIdAndChapterGroupAndChapter(
						subgroupId, 
						chapterGroupId, 
						chapterId
				), 
				"No such subgroup"
		);
	}

	/**
	 * Creates a {@link ChapterSubgroup} and stores it persistently.
	 *
	 * @param chapterId                The ID of the chapter the subgroup should belong to.
	 * @param chapterGroupId           The ID of the chapter group the subgroup should belong to.
	 * @param chapterSubgroupCreateDto The information stored in the subgroup, name and size.
	 * @return The created subgroup.
	 * @throws NoSuchEntityException When there is no chapter group for the given chapter.
	 * @throws EntityExistsException When there is already a subgroup with the same name for the
	 *                               given chapter group.
	 */
	@ApiOperation("Create a chapter subgroup in the given chapter group")
	@PostMapping
	public ChapterSubgroupDetailsDto post(
			@PathVariable int chapterId, @PathVariable int chapterGroupId,
			@RequestBody @Valid ChapterSubgroupCreateDto chapterSubgroupCreateDto) {
		NoSuchEntityException.checkNull(
				chapterGroupDao.findByChapterGroupIdAndChapterId(chapterGroupId, chapterId),
				"No such chapter group");

		boolean exists = chapterSubgroupDao.existsByChapterGroupIdAndName(
				chapterGroupId, chapterSubgroupCreateDto.getName());
		if (exists) {
			throw new EntityExistsException(
					"Subgroup with this name already exists for the given chapter group.");
		}

		ChapterSubgroupDetailsDto chapterSubgroup = new ChapterSubgroupDetailsDto();
		chapterSubgroup.setChapterGroupId(chapterGroupId);
		chapterSubgroup.setName(chapterSubgroupCreateDto.getName());
		chapterSubgroup.setSize(chapterSubgroupCreateDto.getSize());

		final int subgroupId = chapterSubgroupDao.insertAndGetId(chapterSubgroup);
		chapterSubgroup.setId(subgroupId);

		return chapterSubgroup;
	}

	/**
	 * Updates a {@link ChapterSubgroup} by altering it's name and/or size.
	 *
	 * @param chapterId                The ID of the chapter the subgroup belongs to.
	 * @param chapterGroupId           The ID of the chapter group the subgroup belongs to.
	 * @param subgroupId               The ID of the subgroup to update.
	 * @param chapterSubgroupCreateDto The information to update, consists of name and size.
	 * @return The updated subgroup.
	 * @throws NoSuchEntityException When there is no such subgroup.
	 */
	@ApiOperation(value = "Updates a chapter subgroup in the given chapter group.",
			notes = "Enables the altering of name and size field.")
	@PutMapping("/{subgroupId}")
	public ChapterSubgroup update(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int subgroupId,
			@RequestBody @Valid ChapterSubgroupCreateDto chapterSubgroupCreateDto) {
		ChapterSubgroupDetailsDto subgroup = NoSuchEntityException.checkNull(
				chapterSubgroupDao.findByIdAndChapterGroupAndChapter(
						subgroupId, 
						chapterGroupId, 
						chapterId
				),
				"No such subgroup"
		);

		boolean exists = chapterSubgroupDao.existsByChapterGroupIdAndNameAndNotSubgroupId(
				chapterGroupId,
				chapterSubgroupCreateDto.getName(),
				subgroupId
		);
		if (exists) {
			throw new EntityExistsException(
					"Subgroup with this name already exists for the given chapter group."
			);
		}
		
		subgroup.setName(chapterSubgroupCreateDto.getName());
		subgroup.setSize(chapterSubgroupCreateDto.getSize());

		chapterSubgroupDao.update(subgroup);
		return subgroup;
	}

	/**
	 * Deletes a subgroup enrollment.
	 *
	 * @param chapterId      The ID of the chapter the subgroup belongs to.
	 * @param chapterGroupId The ID of the chapter group the subgroup belongs to.
	 * @param subgroupId     The ID of the subgroup to delete.
	 * @throws NoSuchEntityException When there is no such subgroup.
	 */
	@ApiOperation("Delete a subgroup and all related data. This is destructive.")
	@DeleteMapping("/{subgroupId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(
			@PathVariable int chapterId,
			@PathVariable int chapterGroupId,
			@PathVariable int subgroupId) {
		// Ensure that it exists
		get(chapterId, chapterGroupId, subgroupId);
		chapterSubgroupDao.deleteById(subgroupId);
	}
}
