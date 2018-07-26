package nl.javalon.sketchlab.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Date;

/**
 * A notification event. Events are naturally sortable in descending order (most recent first).
 *
 * @author Lukas Miedema
 */
@Getter
@AllArgsConstructor
public abstract class NotificationEvent implements Comparable<NotificationEvent> {
	/**
	 * The ID of the notification event
	 */
	private final int id;
	/**
	 * The date the notification event was sent.
	 */
	@NonNull
	private final Date date;

	/**
	 * Returns the type of the Notification, see {@see NotificationType}
	 *
	 * @return The type of the notification.
	 */
	@JsonProperty
	public abstract NotificationType getNotificationType();

	/**
	 * Compares this NotificationEvent to another. Comparing happens by date.
	 *
	 * @param other The other NotificationEvent.
	 * @return <0 if the date of this event is before the other date, 0 if the date is equal, and >0
	 * if the date is after the other date.
	 */
	@Override
	public int compareTo(@NonNull NotificationEvent other) {
		return other.date.compareTo(this.date);
	}
}
