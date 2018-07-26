package nl.javalon.sketchlab.dto.task;

import lombok.Getter;
import lombok.Setter;
import nl.javalon.sketchlab.entity.tables.pojos.ExampleSubmission;
import nl.javalon.sketchlab.entity.tables.pojos.User;

/**
 * ExampleSubmission with the user who authored it.
 * @author Lukas Miedema
 */
@Getter
@Setter
public class ExampleSubmissionDetailsDto extends ExampleSubmission {
	private User user;
}
