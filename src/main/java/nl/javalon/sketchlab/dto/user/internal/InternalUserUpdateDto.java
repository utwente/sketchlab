package nl.javalon.sketchlab.dto.user.internal;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;

/**
 * DTO used to update an internal user.
 * @author Jelle Stege
 */
@Getter
@Setter
public class InternalUserUpdateDto {
	@NotEmpty
	private String firstName;

	private String lastName;

	@NotEmpty
	@Email
	private String email;

	private String password;

	private Boolean active;

	private Boolean suspended;

	@AssertTrue(message = "Password should be empty or at least 8 characters.")
	public boolean isPasswordValid() {
		return password == null || password.length() >= 8;
	}
}
