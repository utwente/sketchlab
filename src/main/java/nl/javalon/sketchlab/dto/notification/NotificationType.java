package nl.javalon.sketchlab.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Determines the type of the notification. Since each notification is different it's useful to
 * determine the actual type of the notification the user has received.
 *
 * @author Lukas Miedema
 */
@AllArgsConstructor
@Getter
public enum NotificationType {

	SUBMISSION_ANNOTATION(ObjectType.SUBMISSION),
	SUBMISSION_BEST_WORK(ObjectType.SUBMISSION),

	CHAPTER_GROUP_GRADE(ObjectType.CHAPTER_GROUP),
	CHAPTER_GROUP_ENROLL(ObjectType.CHAPTER_GROUP),

	TASK_QUESTION(ObjectType.TASK),
	QUESTION_ANSWER(ObjectType.QUESTION),

	TASK_CREATION(ObjectType.CHAPTER_GROUP);

	/**
	 * The type of object that the notification holds.
	 */
	private final ObjectType objectType;
}
