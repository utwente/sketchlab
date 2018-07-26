package nl.javalon.sketchlab.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Lukas Miedema
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoSuchEntityException extends RuntimeException {

	public NoSuchEntityException() {
		super();
	}

	public NoSuchEntityException(String message) {
		super(message);
	}

	/**
	 * Checks if the entity exists, and if not throws a NoSuchEntityException.
	 * @param entity the entity
	 * @param message to include in the exception
	 * @return the entity, never null.
	 */
	public static <E> E checkNull(E entity, String message) {
		if (entity == null) {
			throw new NoSuchEntityException(message);
		}
		return entity;
	}
}
