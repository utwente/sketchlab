package nl.javalon.sketchlab.dto.user.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import nl.javalon.sketchlab.entity.tables.pojos.InternalUser;
import nl.javalon.sketchlab.entity.tables.pojos.User;

/**
 * @author Jelle Stege
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InternalUserDetailsDto extends InternalUser {
	private User user;

	/**
	 * Override getter to make sure the hashed password is not returned.
	 *
	 * @return null, to make sure there is no actual value in here.
	 */
	@JsonIgnore
	@Override
	public String getPasswordHashed() {
		return null;
	}
}
