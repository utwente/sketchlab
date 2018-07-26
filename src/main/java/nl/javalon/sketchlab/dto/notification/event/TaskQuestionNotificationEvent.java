package nl.javalon.sketchlab.dto.notification.event;

import lombok.Getter;
import nl.javalon.sketchlab.dto.notification.NotificationEvent;
import nl.javalon.sketchlab.dto.notification.NotificationType;
import nl.javalon.sketchlab.entity.tables.pojos.Question;

import java.util.Date;

/**
 * Represents a notification sent when a user has received a question on a chapter group the user
 * is a teaching assistant in or when the user is a teacher.
 *
 * @author Lukas Miedema
 */
@Getter
public class TaskQuestionNotificationEvent extends NotificationEvent {
	/**
	 * The question submitted.
	 */
	private final Question question;

	/**
	 * Instantiates the {@link QuestionAnswerNotificationEvent} with an ID, date and the question
	 * submitted.
	 *
	 * @param notificationId The ID of the notification
	 * @param date           The time this notification was sent.
	 * @param question       The question entered.
	 */
	public TaskQuestionNotificationEvent(int notificationId, Date date, Question question) {
		super(notificationId, date);
		this.question = question;
	}

	/**
	 * Returns the type of the notification
	 *
	 * @return The type of this notification, which is a submitted question.
	 */
	@Override
	public NotificationType getNotificationType() {
		return NotificationType.TASK_QUESTION;
	}
}
