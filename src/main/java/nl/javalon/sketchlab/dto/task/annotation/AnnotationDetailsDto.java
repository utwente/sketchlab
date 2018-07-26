package nl.javalon.sketchlab.dto.task.annotation;

import lombok.Getter;
import lombok.Setter;
import nl.javalon.sketchlab.entity.tables.pojos.Annotation;
import nl.javalon.sketchlab.entity.tables.pojos.User;

/**
 * This DTO is used to hide the "hidden" field.
 *
 * @author Jelle Stege
 */
@Getter
@Setter
public class AnnotationDetailsDto extends Annotation {
	private User user;
}
