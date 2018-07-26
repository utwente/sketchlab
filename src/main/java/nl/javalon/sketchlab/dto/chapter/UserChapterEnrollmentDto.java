package nl.javalon.sketchlab.dto.chapter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.javalon.sketchlab.entity.tables.pojos.Enrollment;
import nl.javalon.sketchlab.entity.tables.pojos.User;

import javax.persistence.Column;

/**
 * DTO which expands a {@link User} to also include the users's enrollment in a chapter group
 * @author Melcher Stikkelorum
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserChapterEnrollmentDto extends User {

	@Column(name = "friendly_id")
	private String friendlyId;

	/**
	 * The enrollment for this user and the chapter group.
	 */
	private Enrollment enrollment;
}
