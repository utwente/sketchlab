package nl.javalon.sketchlab.dto.notification.event;

import lombok.Getter;
import nl.javalon.sketchlab.dto.notification.NotificationEvent;
import nl.javalon.sketchlab.dto.notification.NotificationType;
import nl.javalon.sketchlab.entity.tables.pojos.Answer;

import java.util.Date;

/**
 * Represents a notification sent when a user has received an answer to a question of his.
 *
 * @author Lukas Miedema
 */
@Getter
public class QuestionAnswerNotificationEvent extends NotificationEvent {
	/**
	 * The answer the user received.
	 */
	private final Answer answer;

	/**
	 * Instantiates the {@link QuestionAnswerNotificationEvent} with an ID, date and the answer
	 * the user has received.
	 *
	 * @param notificationId The ID of the notification
	 * @param date           The time this notification was sent.
	 * @param answer         The answer to the user.
	 */
	public QuestionAnswerNotificationEvent(int notificationId, Date date, Answer answer) {
		super(notificationId, date);
		this.answer = answer;
	}

	/**
	 * Returns the type of the notification
	 *
	 * @return The type of this notification, which is an answer to a question
	 */
	@Override
	public NotificationType getNotificationType() {
		return NotificationType.QUESTION_ANSWER;
	}
}
