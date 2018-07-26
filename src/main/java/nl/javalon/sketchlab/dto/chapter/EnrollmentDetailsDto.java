package nl.javalon.sketchlab.dto.chapter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.javalon.sketchlab.entity.tables.pojos.Chapter;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterGroup;
import nl.javalon.sketchlab.entity.tables.pojos.Enrollment;
import nl.javalon.sketchlab.entity.tables.pojos.User;

/**
 * A further expanded {@link Enrollment}, contains the user object and chapter group object.
 *
 * @author Jelle Stege
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EnrollmentDetailsDto extends Enrollment {
	/**
	 * The user object belonging to this enrollment.
	 */
	private User user;
	/**
	 * The chapter group object belonging to this enrollment.
	 */
	private ChapterGroup chapterGroup;
	/**
	 * The chapter object belonging to the chapter group
	 */
	private Chapter chapter;
}
