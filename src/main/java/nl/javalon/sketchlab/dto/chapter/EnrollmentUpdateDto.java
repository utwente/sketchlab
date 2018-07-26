package nl.javalon.sketchlab.dto.chapter;

import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * PUT/POST for an {@link nl.javalon.sketchlab.entity.tables.pojos.Enrollment}, supplies the
 * user defined parameters for the enrollment.
 */
@Data
public class EnrollmentUpdateDto {
	/**
	 * The grade the user received for this chapter group, should be between 0.00 and 10.00.
	 */
	@DecimalMax("10.00")
	@DecimalMin("0.00")
	private BigDecimal grade;

	/**
	 * The message the teacher has supplied along with the grade.
	 */
	private String gradeMessage;

	/**
	 * Indicates whether the user is an assistant or not.
	 */
	private Boolean assistant;


	/**
	 * Determines whether this DTO is valid, which is when either the grade is present or the
	 * assistant is present.
	 *
	 * @return True if this DTO is deemed valid, false if othwerise.
	 */
	@AssertTrue(message = "'grade' or 'assistant' field required.")
	public boolean isValid() {
		return grade != null || assistant != null;
	}
}
