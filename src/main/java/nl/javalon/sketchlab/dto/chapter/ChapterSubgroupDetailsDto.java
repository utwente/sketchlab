package nl.javalon.sketchlab.dto.chapter;

import lombok.Data;
import nl.javalon.sketchlab.entity.tables.pojos.ChapterSubgroup;

import javax.persistence.Column;

/**
 * @author Jelle Stege
 */
@Data
public class ChapterSubgroupDetailsDto extends ChapterSubgroup {
	@Column(name = "enrolled_user_count")
	private int enrolledUserCount;
}
