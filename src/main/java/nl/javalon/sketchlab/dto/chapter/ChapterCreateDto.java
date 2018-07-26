package nl.javalon.sketchlab.dto.chapter;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * POST for a {@link nl.javalon.sketchlab.entity.tables.pojos.Chapter}, this DTO supplies the
 * user defined parameters of the chapter.
 */
@Data
public class ChapterCreateDto {
	/**
	 * Determines the label of the chapter. Should be 32 characters at most.
	 */
	@NotNull
	@Size(min = 1, max = 32)
	private String label;
}
