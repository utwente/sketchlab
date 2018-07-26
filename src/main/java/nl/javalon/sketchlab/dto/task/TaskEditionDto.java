package nl.javalon.sketchlab.dto.task;

import lombok.Getter;
import lombok.Setter;
import nl.javalon.sketchlab.entity.tables.pojos.Task;

import javax.persistence.Column;

/**
 * A {@link Task} object with an attached chapter group id, representing a task in a particular chapter group.
 * @author Lukas Miedema
 */
@Getter
@Setter
public class TaskEditionDto extends Task {

	@Column(name = "chapter_group_id")
	private int chapterGroupId;
}
