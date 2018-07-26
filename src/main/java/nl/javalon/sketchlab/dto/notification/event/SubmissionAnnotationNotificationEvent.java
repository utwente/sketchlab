package nl.javalon.sketchlab.dto.notification.event;

import lombok.Getter;
import nl.javalon.sketchlab.dto.task.annotation.AnnotationDetailsDto;
import nl.javalon.sketchlab.dto.notification.NotificationEvent;
import nl.javalon.sketchlab.dto.notification.NotificationType;

import java.util.Date;

/**
 * Represents a notification sent when a user has received an annotation to a submission of his.
 *
 * @author Lukas Miedema
 */
@Getter
public class SubmissionAnnotationNotificationEvent extends NotificationEvent {
	/**
	 * The annotation entered.
	 */
	private final AnnotationDetailsDto annotation;

	/**
	 * Instantiates the {@link QuestionAnswerNotificationEvent} with an ID, date and the annotation
	 * the user has received.
	 *
	 * @param notificationId The ID of the notification
	 * @param date           The time this notification was sent.
	 * @param annotation     The annotation entered.
	 */
	public SubmissionAnnotationNotificationEvent(
			int notificationId, Date date, AnnotationDetailsDto annotation) {
		super(notificationId, date);
		this.annotation = annotation;
	}

	/**
	 * Returns the type of the notification
	 *
	 * @return The type of this notification, which is an annotation to a submission.
	 */
	@Override
	public NotificationType getNotificationType() {
		return NotificationType.SUBMISSION_ANNOTATION;
	}
}
