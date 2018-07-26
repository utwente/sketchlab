package nl.javalon.sketchlab.dto.user.internal;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * DTO used to create an internal user. Differs from {@link InternalUserUpdateDto} in the fact that
 * password is a required field in this DTO.
 * @author Jelle Stege
 */
@Getter
@Setter
public class InternalUserCreateDto {
	@NotEmpty
	private String firstName;

	private String lastName;

	@NotEmpty
	@Email
	private String email;

	@NotEmpty
	@Length(min = 8, message = "Password must be at least 8 characters.")
	private String password;

	private Boolean active;

	private Boolean suspended;

	public Boolean getActive() {
		return active != null ? active : false;
	}

	public Boolean getSuspended() {
		return suspended != null ? suspended : false;
	}
}
