package nl.javalon.sketchlab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Jelle Stege
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class EntityExistsException extends RuntimeException {

	public EntityExistsException() {
		super();
	}

	public EntityExistsException(String message) {
		super(message);
	}
}
