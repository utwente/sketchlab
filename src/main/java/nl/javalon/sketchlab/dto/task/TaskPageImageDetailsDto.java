package nl.javalon.sketchlab.dto.task;

import lombok.Data;

import javax.persistence.Column;

/**
 * Omits the data field as it has no JSON representation.
 * @author Lukas Miedema
 */
@Data
public class TaskPageImageDetailsDto {
	@Column(name = "id")
	private Integer id;

	@Column(name = "mime_type")
	private String mimeType;
}
