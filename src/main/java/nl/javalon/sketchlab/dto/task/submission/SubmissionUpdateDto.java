package nl.javalon.sketchlab.dto.task.submission;

import lombok.Getter;
import lombok.Setter;
import nl.javalon.sketchlab.entity.tables.pojos.Submission;

import javax.validation.constraints.NotNull;

/**
 * DTO to update a submission.
 * @author Lukas Miedema
 */
@Getter
@Setter
public class SubmissionUpdateDto extends Submission {

	@NotNull
	private Boolean bestWork;

	@NotNull
	private Boolean softDeleted;
}
