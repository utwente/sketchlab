package nl.javalon.sketchlab.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Jelle Stege
 */
@Getter
@Setter
public class PasswordDto {
	@NotEmpty
	private String oldPassword;
	@NotEmpty
	@Length(min = 8)
	private String newPassword;
}
