import {Answer, Question} from "./question";
import {Task} from "./task";
import {AnnotationDetailsDto} from "./submission";

/**
 * A list of {@link NotificationEvent} for one particular object.
 */
export interface NotificationDto {
	objectType: ObjectType;
	object: any;
	notifications: NotificationEvent[];
}

/**
 * Determines the type of the {@link Notification} object.
 */
export enum ObjectType {
	SUBMISSION = "SUBMISSION",
	CHAPTER_GROUP = "CHAPTER_GROUP",
	QUESTION = "QUESTION",
	TASK = "TASK"
}

/**
 * A base notification event.
 */
export interface NotificationEvent {
	id: number;
	date: number;
	notificationType: NotificationType;
}

/**
 * Determines the type of the notification event object.
 */
export enum NotificationType {
	SUBMISSION_ANNOTATION = "SUBMISSION_ANNOTATION",
	SUBMISSION_BEST_WORK = "SUBMISSION_BEST_WORK",

	CHAPTER_GROUP_GRADE = "CHAPTER_GROUP_GRADE",
	CHAPTER_GROUP_ENROLL = "CHAPTER_GROUP_ENROLL",

	TASK_QUESTION = "TASK_QUESTION",
	QUESTION_ANSWER = "QUESTION_ANSWER",

	TASK_CREATION = "TASK_CREATION"
}

export interface ChapterGroupEnrollNotificationEvent extends NotificationEvent {}
export interface ChapterGroupGradeNotificationEvent extends NotificationEvent {
	grade: string
}
export interface QuestionAnswerNotificationEvent extends NotificationEvent {
	answer: Answer;
}
export interface SubmissionAnswerNotificationEvent extends NotificationEvent {
	annotation: AnnotationDetailsDto;
}
export interface SubmissionBestWorkNotificationEvent extends NotificationEvent {}
export interface TaskCreationNotificationEvent extends NotificationEvent {
	task: Task;
}
export interface TaskQuestionNotificationEvent extends NotificationEvent {
	question: Question;
}