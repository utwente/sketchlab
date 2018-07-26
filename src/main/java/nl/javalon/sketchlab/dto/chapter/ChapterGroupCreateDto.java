package nl.javalon.sketchlab.dto.chapter;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * POST for a {@link nl.javalon.sketchlab.entity.tables.pojos.ChapterGroup}, this DTO determines
 * the user defined parameters of the chapter group.
 */
@Data
public class ChapterGroupCreateDto {
	/**
	 * The name of the chapter group, should be 32 symbols at most.
	 */
	@NotNull
	@Size(min = 1, max = 32)
	private String name;
}
