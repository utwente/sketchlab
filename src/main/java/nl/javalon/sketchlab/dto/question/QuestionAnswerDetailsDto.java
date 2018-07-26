package nl.javalon.sketchlab.dto.question;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.javalon.sketchlab.dto.question.AnswerDetailsDto;
import nl.javalon.sketchlab.dto.question.QuestionDetailsDto;

import java.util.List;

/**
 * @author Jelle Stege
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionAnswerDetailsDto extends QuestionDetailsDto {
	private List<AnswerDetailsDto> answers;
}
