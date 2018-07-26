package nl.javalon.sketchlab.dto.task;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;

@Data
@AllArgsConstructor
public class TaskDetailsDto {
	@Column(name  = "id")
	private Integer id;

	@Column(name = "slot")
	private Integer slot;

	@Column(name = "name")
	private String name;

	@Column(name = "track")
	private String track;

	@Column(name = "submitted")
	private Boolean submitted;
}
