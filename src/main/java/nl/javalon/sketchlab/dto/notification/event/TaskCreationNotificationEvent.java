package nl.javalon.sketchlab.dto.notification.event;

import lombok.Getter;
import nl.javalon.sketchlab.dto.notification.NotificationEvent;
import nl.javalon.sketchlab.dto.notification.NotificationType;
import nl.javalon.sketchlab.entity.tables.pojos.Question;
import nl.javalon.sketchlab.entity.tables.pojos.Task;

import java.util.Date;

/**
 * Represents a notification sent when task was created on a chapter group the user
 * is enrolled in.
 *
 * @author Melcher Stikkelorum
 */
@Getter
public class TaskCreationNotificationEvent extends NotificationEvent {

	private final Task task;

	/**
	 * Instantiates the {@link QuestionAnswerNotificationEvent} with an ID an date.
	 *
	 * @param notificationId The ID of the notification
	 * @param date           The time this notification was sent.
	 * @param task			 The newly created task.
	 */
	public TaskCreationNotificationEvent(int notificationId, Date date, Task task) {
		super(notificationId, date);
		this.task = task;
	}

	/**
	 * Returns the type of the notification
	 *
	 * @return The type of this notification, which is a submitted question.
	 */
	@Override
	public NotificationType getNotificationType() {
		return NotificationType.TASK_CREATION;
	}
}
