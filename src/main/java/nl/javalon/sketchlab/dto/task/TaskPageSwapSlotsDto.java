package nl.javalon.sketchlab.dto.task;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * DTO used to swap the slots on two task pages.
 *
 * @author Jelle Stege
 */
@Data
public class TaskPageSwapSlotsDto {
	@NotNull
	private Integer firstTaskPage;
	@NotNull
	private Integer secondTaskPage;
}
