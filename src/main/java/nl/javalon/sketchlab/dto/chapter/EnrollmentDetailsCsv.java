package nl.javalon.sketchlab.dto.chapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Backing DTO used for exporting enrollment data to CSV using
 * {@link nl.javalon.sketchlab.service.CsvService}.
 *
 * @author Melcher Stikkelorum
 */
@Data
public class EnrollmentDetailsCsv {

	/**
	 * The student's friendly Id (utwente ID/email) - if any
	 */
	@CsvBindByPosition(position = 0)
	@JsonProperty("Friendly ID")
	String friendlyId;

	/**
	 * The first name of the user.
	 */
	@CsvBindByPosition(position = 1)
	@JsonProperty("First name")
	String firstName;

	/**
	 * The last name of the user.
	 */
	@CsvBindByPosition(position = 2)
	@JsonProperty("Last name")
	String lastName;

	/**
	 * The grade the user received for this enrollment, or null if non existent.
	 */
	@CsvBindByPosition(position = 3)
	@JsonPropertyOrder("Grade")
	BigDecimal grade;

	/**
	 * The message a teacher has supplied with the grade, or null if non existent.
	 */
	@CsvBindByPosition(position = 4)
	@JsonProperty("Message")
	String gradeMessage;

	/**
	 * Boolean indicating whether this user is a teaching assistant.
	 */
	@CsvBindByPosition(position = 5)
	@JsonProperty("Assistant")
	Boolean assistant;
}
