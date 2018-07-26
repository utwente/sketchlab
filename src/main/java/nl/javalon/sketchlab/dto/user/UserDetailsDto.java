package nl.javalon.sketchlab.dto.user;

import lombok.Getter;
import lombok.Setter;
import nl.javalon.sketchlab.entity.tables.pojos.User;

import javax.persistence.Column;

/**
 * @author Jelle Stege
 */
@Getter
@Setter
public class UserDetailsDto extends User {
	@Column(name = "friendly_id")
	private String friendlyId;
	@Column(name = "ta")
	private Boolean ta;
}
