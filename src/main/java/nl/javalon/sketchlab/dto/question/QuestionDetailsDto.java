package nl.javalon.sketchlab.dto.question;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.javalon.sketchlab.entity.tables.pojos.Question;
import nl.javalon.sketchlab.entity.tables.pojos.Task;
import nl.javalon.sketchlab.entity.tables.pojos.User;

/**
 * @author Jelle Stege
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionDetailsDto extends Question {
	private User user;
	private Task task;
}
