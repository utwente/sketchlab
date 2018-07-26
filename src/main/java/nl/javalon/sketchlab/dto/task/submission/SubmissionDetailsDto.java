package nl.javalon.sketchlab.dto.task.submission;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import nl.javalon.sketchlab.entity.tables.pojos.Submission;
import nl.javalon.sketchlab.entity.tables.pojos.Task;
import nl.javalon.sketchlab.entity.tables.pojos.User;

import javax.persistence.Column;

/**
 * Submission with the number of votes.
 * @author Lukas Miedema
 */
@Getter
@Setter
public class SubmissionDetailsDto extends Submission {

	@Column(name = "best_work")
	private Boolean bestWork;

	@Column(name = "votes")
	private Integer votes;

	@Column(name = "annotations")
	private Integer annotations;

	@Column(name = "user_has_voted")
	private Boolean userHasVoted;

	@Column(name = "soft_deleted")
	private Boolean softDeleted;

	private User user;

	private Task task;
}
