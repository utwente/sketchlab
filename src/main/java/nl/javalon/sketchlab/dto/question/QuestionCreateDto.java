package nl.javalon.sketchlab.dto.question;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Melcher Stikkelorum
 */
@Data
public class QuestionCreateDto {

	@NotNull
	private String text;

	@NotNull
	private Integer taskId;
}
