package nl.javalon.sketchlab.dto.task.annotation;

import lombok.Data;

import javax.validation.constraints.AssertTrue;

/**
 * Annotation object used during creating.
 *
 * @author Jelle Stege
 */
@Data
public class AnnotationCreateDto {
	private String drawing;
	private String comment;

	/**
	 * Checks if this Dto is valid. Considered valid if at least one field in this DTO is not null
	 * and not emptyfilled
	 *
	 * @return True if valid, false if otherwise.
	 */
	@AssertTrue(message = "'drawing' or 'comment' field required.")
	protected boolean isValid() {
		return (drawing != null && !drawing.isEmpty()) || (comment != null && !comment.isEmpty());
	}
}
