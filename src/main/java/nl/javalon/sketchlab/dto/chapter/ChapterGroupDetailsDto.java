package nl.javalon.sketchlab.dto.chapter;

import lombok.*;
import nl.javalon.sketchlab.dto.task.TaskDetailsDto;
import nl.javalon.sketchlab.entity.tables.pojos.Chapter;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterGroup;

import java.util.*;

/**
 * DTO for Chapter groups, holds the parent chapter and the tasks/tracks that belong to it.
 */
@Getter
@Setter
@AllArgsConstructor
public class ChapterGroupDetailsDto {
	/**
	 * The chapter this chapter group belongs to.
	 */
	@NonNull
	private Chapter chapter;

	/**
	 * The chapter group. Can be null when this DTO is used to serve just chapter details.
	 */
	private ChapterGroup chapterGroup;

	/**
	 * The role the receiver has.
	 */
	@NonNull
	private ChapterGroupRole role;

	/**
	 * The tracks in the corresponding chapter, with tasks in it.
	 */
	@NonNull
	private Map<String, List<TaskDetailsDto>> tracks;
}
