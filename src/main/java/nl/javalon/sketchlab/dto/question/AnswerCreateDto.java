package nl.javalon.sketchlab.dto.question;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Melcher Stikkelorum
 */
@Data
public class AnswerCreateDto {

	@NotNull
	private String text;
}
