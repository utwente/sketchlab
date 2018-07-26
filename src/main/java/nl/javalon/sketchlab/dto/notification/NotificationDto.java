package nl.javalon.sketchlab.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * A list of {@link NotificationEvent} for one particular object.
 *
 * @author Lukas Miedema
 */
@AllArgsConstructor
@Getter
public class NotificationDto implements Comparable<NotificationDto> {
	/**
	 * The notificiation's object type.
	 */
	private final ObjectType objectType;

	/**
	 * The notification's object.
	 */
	private final Object object;

	/**
	 * The list of notifications for the object
	 */
	private final List<NotificationEvent> notifications;

	/**
	 * Compares two {@link NotificationDto} objects by their {@link NotificationEvent}s
	 *
	 * @param other The other NotificationDto
	 * @return <0 if this dto is considered smaller than the other, 0 if they are deemed equal and
	 * >0 if this dto is larger.
	 */
	@Override
	public int compareTo(@NonNull NotificationDto other) {
		if (notifications.isEmpty() && other.getNotifications().isEmpty()) {
			return 0;
		}
		if (notifications.isEmpty()) {
			return -1;
		}
		if (other.getNotifications().isEmpty()) {
			return 1;
		}
		return notifications.get(0).compareTo(other.getNotifications().get(0));
	}
}
