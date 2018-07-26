package nl.javalon.sketchlab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Melcher Stikkelorum
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoAccessException extends RuntimeException {

	public NoAccessException() {
		super();
	}

	public NoAccessException(String message) {
		super(message);
	}
}
