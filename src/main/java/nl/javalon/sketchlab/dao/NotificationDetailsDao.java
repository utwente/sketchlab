package nl.javalon.sketchlab.dao;

import nl.javalon.sketchlab.dto.notification.NotificationDto;
import nl.javalon.sketchlab.dto.notification.NotificationEvent;
import nl.javalon.sketchlab.dto.notification.NotificationType;
import nl.javalon.sketchlab.dto.notification.event.*;
import nl.javalon.sketchlab.dto.task.TaskEditionDto;
import nl.javalon.sketchlab.entity.tables.daos.NotificationDao;
import nl.javalon.sketchlab.entity.tables.pojos.*;
import nl.javalon.sketchlab.security.UserRole;
import nl.javalon.sketchlab.utils.Pair;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.javalon.sketchlab.entity.Tables.*;

/**
 * Notification DAO for retrieving and deleting notifications.
 *
 * @author Lukas Miedema
 */
@Repository
public class NotificationDetailsDao extends NotificationDao {

	private final DSLContext sql;
	private final EnrollmentDetailsDao enrollmentDao;
	private final UserDetailsDao userDao;

	/**
	 * Instantiates the {@link NotificationDetailsDao} using a jOOQ {@link Configuration} and the
	 * used {@link DSLContext}. Furthermore, needs an {@link EnrollmentDetailsDao} and
	 * {@link UserDetailsDao} to retrieve users to send notifications to.
	 *
	 * @param configuration The used jOOQ configuration.
	 * @param sql           The used DSL context.
	 * @param enrollmentDao The DAO used to fetch enrollments with.
	 * @param userDao       The DAO used to fetch users with.
	 */
	@Autowired
	public NotificationDetailsDao(
			Configuration configuration,
			DSLContext sql,
			EnrollmentDetailsDao enrollmentDao,
			UserDetailsDao userDao) {
		super(configuration);
		this.sql = sql;
		this.enrollmentDao = enrollmentDao;
		this.userDao = userDao;
	}

	/**
	 * Creates a notification for users which are enrolled in a chapter group.
	 *
	 * @param chapterGroupId The chapter group into which the enrolledUserId is enrolled
	 * @param enrolledUserId The ID of the user which was enrolled.
	 */
	public void handleChapterGroupEnrollEvent(int chapterGroupId, UUID enrolledUserId) {
		Notification notification = create(enrolledUserId, NotificationType.CHAPTER_GROUP_ENROLL);
		notification.setChapterGroupId(chapterGroupId);
		this.insert(notification);
	}

	/**
	 * Creates a notification for users which have just received a grade on a chapter group.
	 *
	 * @param chapterGroupId The chapter group for which the user has received a grade.
	 * @param enrolledUserId The ID of the user which has received a grade.
	 */
	public void handleChapterGroupGradeEvent(int chapterGroupId, UUID enrolledUserId) {
		Notification notification = create(enrolledUserId, NotificationType.CHAPTER_GROUP_GRADE);
		notification.setChapterGroupId(chapterGroupId);
		this.insert(notification);
	}

	/**
	 * Creates a notification when a question has received an answer.
	 *
	 * @param question The existing question for which to create an answer notification.
	 * @param answerId The ID of the answer.
	 */
	public void handleQuestionAnswerEvent(Question question, int answerId) {
		Notification notification = create(question.getUserId(), NotificationType.QUESTION_ANSWER);
		notification.setQuestionId(question.getId());
		notification.setQuestionAnswerId(answerId);
		this.insert(notification);
	}

	/**
	 * Creates notifications for all teachers and TAs (which are enrolled in the given chapter
	 * group) when a user asks a question.
	 *
	 * @param question The question for which to add notifications.
	 */
	public void handleQuestionCreateEvent(Question question) {
		Notification notification = create(null, NotificationType.TASK_QUESTION);
		notification.setQuestionId(question.getId());

		// Send this to every TA and teacher
		Stream<UUID> teacherIds = userDao
				.fetchByRole(UserRole.TEACHER.toString())
				.stream()
				.map(User::getId);
		Stream<UUID> assistantIds = enrollmentDao
				.fetchAssistantsById(question.getChapterGroupId(), false)
				.stream()
				.map(Enrollment::getUserId);

		Stream<UUID> recipients = Stream.concat(teacherIds, assistantIds).distinct();
		recipients.forEach(userId -> {
			notification.setUserId(userId);
			this.insert(notification);
		});
	}

	/**
	 * Creates notification for all  users enrolled in the chapter group to which this task was added.
	 *
	 * @param task The newly created task for which to add notifications.
	 */
	public void handleTaskCreationEvent(Task task) {
		Notification notification = create(null, NotificationType.TASK_CREATION);
		notification.setTaskId(task.getId());

		// Send this to all enrolled users
		Stream<Pair<UUID, Integer>> enrolledIds = enrollmentDao
				.fetchByChapterId(task.getChapterId())
				.stream()
				.map(enrollment -> new Pair<>(enrollment.getUserId(), enrollment.getChapterGroupId()));
		enrolledIds.forEach(pair -> {
			notification.setUserId(pair.getFirst());
			notification.setChapterGroupId(pair.getSecond());
			this.insert(notification);
		});
	}

	/**
	 * Creates a notification for a user which has just received an annotation to a given submission
	 * of his.
	 *
	 * @param submission   The submission which has just received an annotation. The user is
	 *                     extracted from this submission.
	 * @param annotationId The annotation which was just created.
	 */
	public void handleSubmissionAnnotationEvent(Submission submission, int annotationId) {
		Notification notification = create(submission.getUserId(),
				NotificationType.SUBMISSION_ANNOTATION);
		notification.setSubmissionId(submission.getId());
		notification.setSubmissionAnnotationId(annotationId);
		this.insert(notification);
	}

	/**
	 * Creates a notification for when a submission is marked as best work.
	 *
	 * @param submission The submission which was marked as best work. The user is extracted from
	 *                   this submission.
	 */
	public void handleSubmissionBestWorkEvent(Submission submission) {
		Notification notification = create(submission.getUserId(),
				NotificationType.SUBMISSION_BEST_WORK);
		notification.setSubmissionId(submission.getId());
		this.insert(notification);
	}


	/**
	 * Delete a notification if the given user ID is equal to the ID in the notification.
	 *
	 * @param userId         The ID of the user.
	 * @param notificationId The ID of the notification.
	 */
	public void deleteUserAndById(UUID userId, int notificationId) {
		sql.deleteFrom(NOTIFICATION)
				.where(NOTIFICATION.USER_ID.eq(userId))
				.and(NOTIFICATION.ID.eq(notificationId))
				.execute();
	}

	/**
	 * Get a list of all notifications with related events.
	 *
	 * @param userId The user ID for which to receive notifications.
	 * @return A List of all notifications for the given user.
	 */
	public List<NotificationDto> getNotificationsForUser(UUID userId) {

		// Create table aliases that will be queried twice for different relations
		nl.javalon.sketchlab.entity.tables.Task submissionTaskTable = TASK.as("SUBMISSION_TASK");
		nl.javalon.sketchlab.entity.tables.User submissionUserTable = USER.as("SUBMISSION_USER");

		nl.javalon.sketchlab.entity.tables.Task creationTaskTable = TASK.as("CREATION_TASK");

		nl.javalon.sketchlab.entity.tables.User annotationUserTable = USER.as("ANNOTATION_USER");

		nl.javalon.sketchlab.entity.tables.User questionUserTable = USER.as("QUESTION_USER");
		nl.javalon.sketchlab.entity.tables.User answerUserTable = USER.as("ANSWER_USER");

		List<Pair<Object, NotificationEvent>> events = sql
				.select(NOTIFICATION.ID, NOTIFICATION.EVENT_TYPE, NOTIFICATION.DATE)

				// submission {best work, annotation}
				.select(SUBMISSION.fields())
				.select(SubmissionDetailsDao.VOTE_COUNT)
				.select(SubmissionDetailsDao.userHasVoted(userId))
				.select(submissionTaskTable.fields())
				.select(submissionUserTable.fields())
				.select(creationTaskTable.fields())

				.select(ANNOTATION.ID, // everything but 'drawing'
						ANNOTATION.SUBMISSION_ID,
						ANNOTATION.SOFT_DELETED,
						ANNOTATION.COMMENT,
						ANNOTATION.CREATED_AT,
						ANNOTATION.FLIP_XY,
						ANNOTATION.INVERT_X,
						ANNOTATION.INVERT_Y,
						ANNOTATION.LAST_UPDATED_AT) // submission annotation
				.select(annotationUserTable.fields())

				.select(QUESTION.fields()) // question {create, answer}
				.select(questionUserTable.fields())

				.select(ANSWER.fields()) // question answer
				.select(answerUserTable.fields())

				.select(TASK.fields()) // question create
				.select(CHAPTER_GROUP.fields()) // chapter group {grade, enroll}
				.select(ENROLLMENT.GRADE) // chapter group grade

				.from(NOTIFICATION)

				.leftJoin(creationTaskTable).on(creationTaskTable.ID.eq(NOTIFICATION.TASK_ID))

				.leftJoin(SUBMISSION
						.join(submissionTaskTable).on(SUBMISSION.TASK_ID.eq(submissionTaskTable.ID))
						.join(submissionUserTable).on(SUBMISSION.USER_ID.eq(submissionUserTable.ID))
				).on(NOTIFICATION.SUBMISSION_ID.eq(SUBMISSION.ID))

				.leftJoin(ANNOTATION
						.join(annotationUserTable).on(ANNOTATION.USER_ID.eq(annotationUserTable.ID))
				).on(NOTIFICATION.SUBMISSION_ANNOTATION_ID.eq(ANNOTATION.ID))

				.leftJoin(QUESTION
						.join(questionUserTable).on(QUESTION.USER_ID.eq(questionUserTable.ID))
				).on(NOTIFICATION.QUESTION_ID.eq(QUESTION.ID))

				.leftJoin(TASK).on(QUESTION.TASK_ID.eq(TASK.ID))

				.leftJoin(ANSWER
						.join(answerUserTable).on(ANSWER.USER_ID.eq(answerUserTable.ID))
				).on(NOTIFICATION.QUESTION_ANSWER_ID.eq(ANSWER.ID))

				.leftJoin(CHAPTER_GROUP).on(NOTIFICATION.CHAPTER_GROUP_ID.eq(CHAPTER_GROUP.ID))
				.leftJoin(ENROLLMENT)
				.on(NOTIFICATION.CHAPTER_GROUP_ID.eq(ENROLLMENT.CHAPTER_GROUP_ID))
				.and(NOTIFICATION.USER_ID.eq(ENROLLMENT.USER_ID))

				.where(NOTIFICATION.USER_ID.eq(userId))
				.and(SUBMISSION.SOFT_DELETED.isNull().or(SUBMISSION.SOFT_DELETED.equal(false)))
				.and(ANNOTATION.SOFT_DELETED.isNull().or(ANNOTATION.SOFT_DELETED.equal(false)))

				.fetch(record -> {

					// Basics
					int id = record.getValue(NOTIFICATION.ID);
					NotificationType notificationType =
							NotificationType.valueOf(record.getValue(NOTIFICATION.EVENT_TYPE));
					Timestamp date = record.getValue(NOTIFICATION.DATE);

					// Specifics
					switch (notificationType) {
						case SUBMISSION_ANNOTATION:
							return new Pair<>(
									SubmissionDetailsDao.mapSubmissionDetailsDto(
											record, submissionTaskTable, submissionUserTable),
									new SubmissionAnnotationNotificationEvent(id, date,
											AnnotationDetailsDao.mapAnnotationDetailsDto(
													record, annotationUserTable)));

						case SUBMISSION_BEST_WORK:
							return new Pair<>(
									SubmissionDetailsDao.mapSubmissionDetailsDto(
											record, submissionTaskTable, submissionUserTable),
									new SubmissionBestWorkNotificationEvent(id, date));

						case CHAPTER_GROUP_GRADE:
							return new Pair<>(
									record.into(CHAPTER_GROUP.fields()).into(ChapterGroup.class),
									new ChapterGroupGradeNotificationEvent(id, date,
											record.getValue(ENROLLMENT.GRADE)));

						case CHAPTER_GROUP_ENROLL:
							return new Pair<>(
									record.into(CHAPTER_GROUP.fields()).into(ChapterGroup.class),
									new ChapterGroupEnrollNotificationEvent(id, date));

						case TASK_QUESTION:
							return new Pair<>(
									record.into(TASK.ID, TASK.CHAPTER_ID, TASK.TRACK, TASK.SLOT, TASK.AUTHOR_ID,
											TASK.NAME, QUESTION.CHAPTER_GROUP_ID).into(TaskEditionDto.class),
									new TaskQuestionNotificationEvent(id, date,
											QuestionDetailsDao.mapQuestionDetailsDto(record, TASK, questionUserTable)));
						case TASK_CREATION:
							Task task = record.into(creationTaskTable.fields()).into(Task.class);
							return new Pair<>(
									record.into(CHAPTER_GROUP.fields()).into(ChapterGroup.class),
									new TaskCreationNotificationEvent(id, date, task));

						case QUESTION_ANSWER:
							return new Pair<>(
									QuestionDetailsDao.mapQuestionDetailsDto(
											record, TASK, questionUserTable),
									new QuestionAnswerNotificationEvent(id, date,
											AnswerDetailsDao.mapAnswerDetailsDto(
													record, ANSWER, answerUserTable)));

						default:
							throw new IllegalStateException("Cannot process event type: " +
									notificationType);
					}
				});

		// Associate the objects
		Map<Object, List<NotificationEvent>> grouped = events.stream().collect(
				Collectors.groupingBy(
						Pair::getFirst,
						Collectors.mapping(Pair::getSecond, Collectors.toList())));

		Stream<NotificationDto> notifications = grouped.entrySet().stream().map(entry -> {
			Object object = entry.getKey();
			List<NotificationEvent> notificationList = entry.getValue();

			// Sort all the events so that the top event is the most recent one
			Collections.sort(notificationList);

			NotificationEvent head = notificationList.get(0); // always at least one event
			return new NotificationDto(
					head.getNotificationType().getObjectType(), object, notificationList);
		});

		return notifications.sorted().collect(Collectors.toList());
	}

	/**
	 * Utility method for creating a new notification with just the user id and the event type set.
	 *
	 * @param recipientUserId  The user ID of the user which receives a notification.
	 * @param notificationType The event type to set.
	 * @return A new notification object.
	 */
	private Notification create(UUID recipientUserId, NotificationType notificationType) {
		Notification notification = new Notification();
		notification.setUserId(recipientUserId);
		notification.setEventType(notificationType.toString());
		notification.setDate(new Timestamp(System.currentTimeMillis()));
		return notification;
	}
}
