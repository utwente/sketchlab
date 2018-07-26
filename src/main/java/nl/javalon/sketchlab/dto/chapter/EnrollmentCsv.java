package nl.javalon.sketchlab.dto.chapter;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * Backing DTO used for importing enrollment data from CSV using
 * {@link nl.javalon.sketchlab.service.CsvService}.
 *
 * @author Melcher Stikkelorum
 */
@Data
public class EnrollmentCsv {

	/**
	 * The friendly ID of the user. UT id for UTwente users, email for internal users.
	 */
	@CsvBindByName(column = "Student Number", required = true)
	String studentNumber;

	/**
	 * Whether or not the user is a student assistant.
	 */
	@CsvBindByName(column = "Course Role")
	String role;
}
