package nl.javalon.sketchlab.dto.task;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * POST and PUT for task pages. It's missing the id, task_id and author_id fields, which are added
 * through URL variables or the security context. Note that both text and videoUrl are optional
 * elements.
 *
 * @author Jelle Stege
 */
@Data
public class TaskPageCreateDto {
	@NotNull
	@Size(min = 1, max = 100)
	private String title;

	private String text;

	private String videoUrl;

	private Integer slot;
}
