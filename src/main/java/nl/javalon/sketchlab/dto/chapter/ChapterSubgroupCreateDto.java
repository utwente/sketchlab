package nl.javalon.sketchlab.dto.chapter;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * POST for a {@link nl.javalon.sketchlab.entity.tables.pojos.ChapterSubgroup}, this DTO supplies
 * the user defined parameters of the subgroup.
 */
@Data
public class ChapterSubgroupCreateDto {
	/**
	 * The name of the chapter group. Should be 32 characters at most.
	 */
	@NotNull
	@Size(min = 1, max = 32)
	private String name;

	/**
	 * The size of the subgroup, should be at least 1. Note that this field may be null, if so, the
	 * size of the group is unlimited.
	 */
	@Min(1)
	@Max(Integer.MAX_VALUE)
	private Integer size;
}
