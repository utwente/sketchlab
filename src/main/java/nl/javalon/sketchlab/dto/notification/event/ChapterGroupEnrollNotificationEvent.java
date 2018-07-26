package nl.javalon.sketchlab.dto.notification.event;

import nl.javalon.sketchlab.dto.notification.NotificationEvent;
import nl.javalon.sketchlab.dto.notification.NotificationType;

import java.util.Date;

/**
 * Represents a notification sent when a user is enrolled in a chapter group.
 *
 * @author Lukas Miedema
 */
public class ChapterGroupEnrollNotificationEvent extends NotificationEvent {

	/**
	 * Instantiates the {@link ChapterGroupEnrollNotificationEvent} with a given notification ID and
	 * the time this notification is sent.
	 *
	 * @param notificationId The ID of the notification.
	 * @param date           The time this notification was sent.
	 */
	public ChapterGroupEnrollNotificationEvent(int notificationId, Date date) {
		super(notificationId, date);
	}

	/**
	 * Returns the type of the notification
	 *
	 * @return The type of this notification, which is a chapter group enrollment.
	 */
	@Override
	public NotificationType getNotificationType() {
		return NotificationType.CHAPTER_GROUP_ENROLL;
	}
}
