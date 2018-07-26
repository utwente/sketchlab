package nl.javalon.sketchlab.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author Lukas Miedema
 */
@Getter
@Setter
public class PasswordResetDto {

	@NotNull
	@NotEmpty
	@Email
	private String email;
}
