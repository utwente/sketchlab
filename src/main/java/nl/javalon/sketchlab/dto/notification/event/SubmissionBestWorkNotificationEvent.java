package nl.javalon.sketchlab.dto.notification.event;

import nl.javalon.sketchlab.dto.notification.NotificationEvent;
import nl.javalon.sketchlab.dto.notification.NotificationType;

import java.util.Date;

/**
 * Represents a notification sent when a user has received a best work mark on a submission of his.
 *
 * @author Lukas Miedema
 */
public class SubmissionBestWorkNotificationEvent extends NotificationEvent {

	/**
	 * Instantiates the {@link SubmissionBestWorkNotificationEvent} with an ID and date
	 *
	 * @param notificationId The ID of the notification
	 * @param date           The time this notification was sent.
	 */
	public SubmissionBestWorkNotificationEvent(int notificationId, Date date) {
		super(notificationId, date);
	}

	/**
	 * Returns the type of the notification
	 *
	 * @return The type of this notification, which is an best work mark on a submission.
	 */
	@Override
	public NotificationType getNotificationType() {
		return NotificationType.SUBMISSION_BEST_WORK;
	}
}
