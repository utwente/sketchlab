package nl.javalon.sketchlab.dto.task;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * POST for task. It's missing the id, chapter_id, author_id fields.
 */
@Data
public class TaskCreateDto {

	@NotNull
	@Size(min = 1, max = 32)
	private String name;

	@NotNull
	private TaskTrack track;

	@NotNull
	private Integer slot;
}
