package nl.javalon.sketchlab.dto.notification.event;

import lombok.Getter;
import nl.javalon.sketchlab.dto.notification.NotificationEvent;
import nl.javalon.sketchlab.dto.notification.NotificationType;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a notification sent when a user has received a grade for enrollment.
 *
 * @author Lukas Miedema
 */
@Getter
public class ChapterGroupGradeNotificationEvent extends NotificationEvent {
	/**
	 * The grade the user has received.
	 */
	private final BigDecimal grade;

	/**
	 * Instantiates the {@link ChapterGroupGradeNotificationEvent} with an ID, date and the grade
	 * the user has received.
	 *
	 * @param notificationId The ID of the notification
	 * @param date           The time this notification was sent.
	 * @param grade          The grade the user has received.
	 */
	public ChapterGroupGradeNotificationEvent(int notificationId, Date date, BigDecimal grade) {
		super(notificationId, date);
		this.grade = grade;
	}

	/**
	 * Returns the type of the notification
	 *
	 * @return The type of this notification, which is a grade notification.
	 */
	@Override
	public NotificationType getNotificationType() {
		return NotificationType.CHAPTER_GROUP_GRADE;
	}
}
