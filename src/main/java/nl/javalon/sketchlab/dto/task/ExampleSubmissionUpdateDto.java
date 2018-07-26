package nl.javalon.sketchlab.dto.task;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Update dto for the ExampleSubmission.
 * @author Lukas Miedema
 */
@Getter
@Setter
public class ExampleSubmissionUpdateDto {
	private String comment = "";
}
