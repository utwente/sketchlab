package nl.javalon.sketchlab.dto.task.annotation;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.AssertTrue;

/**
 * Annotation object used during updating.
 *
 * @author Jelle Stege
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnnotationUpdateDto extends AnnotationCreateDto {
	private Boolean softDeleted;

	/**
	 * Checks if this Dto is valid. Considered valid if at least one field in this DTO is not null
	 * and not emptyfilled
	 *
	 * @return True if valid, false if otherwise.
	 */
	@Override
	@AssertTrue(message = "'drawing', 'comment' or 'soft-deleted' field required.")
	protected boolean isValid() {
		return super.isValid() || softDeleted != null;
	}
}
