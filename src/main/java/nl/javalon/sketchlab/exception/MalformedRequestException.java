package nl.javalon.sketchlab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Melcher Stikkelorum
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MalformedRequestException extends RuntimeException {

	public MalformedRequestException() {
		super();
	}

	public MalformedRequestException(String message) {
		super(message);
	}
}
