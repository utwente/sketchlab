package nl.javalon.sketchlab.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import java.util.UUID;

/**
 * @author Jelle Stege
 */
@Getter
@Setter
public class UserActivationDto {
	private static final String UUID_PATTERN =
			"^[0-9a-fA-f]{8}-[0-9a-fA-f]{4}-[0-9a-fA-f]{4}-[0-9a-fA-f]{4}-[0-9a-fA-f]{12}$";		
	@Pattern(regexp = UUID_PATTERN, message = "Invalid user ID")
	private String userId;
	@NotEmpty
	private String token;

	public UUID getUserId() {
		return UUID.fromString(userId);
	}
}
