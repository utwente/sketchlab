package nl.javalon.sketchlab.dto.question;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.javalon.sketchlab.entity.tables.pojos.Answer;
import nl.javalon.sketchlab.entity.tables.pojos.User;

/**
 * @author Jelle Stege
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AnswerDetailsDto extends Answer {
	private User user;
}
