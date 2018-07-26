package nl.javalon.sketchlab.dto.user.internal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nl.javalon.sketchlab.entity.tables.pojos.InternalUser;
import nl.javalon.sketchlab.entity.tables.pojos.User;

/**
 * DTO extending regular {@link InternalUser}, contains field with {@link User} object.
 *
 * @author Jelle Stege
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class InternalUserDetailsCreationDto extends InternalUser {
	private User user;

	public InternalUserDetailsCreationDto(InternalUser value, User user) {
		super(value);
		this.user = user;
	}
}
