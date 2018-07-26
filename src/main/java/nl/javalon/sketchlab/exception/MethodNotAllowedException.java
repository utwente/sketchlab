package nl.javalon.sketchlab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Lukas Miedema.
 */
@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class MethodNotAllowedException extends RuntimeException {

	public MethodNotAllowedException(String message) {
		super(message);
	}
}
