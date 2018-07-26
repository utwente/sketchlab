import {ObjectType} from "../../../_dto/notification";
import {Component, Type} from "@angular/core";
import {QuestionDetailsDto} from "../../../_dto/question";
import {SubmissionDetails} from "../../../_dto/submission";
import {Task, TaskEdition} from "app/_dto/task";
import {ChapterGroup} from "../../../_dto/chapter";
import {Router} from "@angular/router";

/**
 * A notification object covers a set of events around the same notification object (for instance a Task, or an
 * Enrollment). This is the base component for rendering the title for such an object. Instances are loaded dynamically
 * by the {@link NotificationObjectComponent}.
 */
export abstract class NotificationObjectTitle<T> {
	public objectType: ObjectType;
	public object: T;

	private static mapping: Map<ObjectType, Type<NotificationObjectTitle<any>>>;

	static initialize() {
		const mapping: Map<ObjectType, any> = new Map();

		mapping.set(ObjectType.SUBMISSION, SubmissionNotificationObjectTitleComponent);
		mapping.set(ObjectType.CHAPTER_GROUP, ChapterGroupNotificationObjectTitleComponent);
		mapping.set(ObjectType.QUESTION, QuestionNotificationObjectTitleComponent);
		mapping.set(ObjectType.TASK, TaskNotificationObjectTitleComponent);

		NotificationObjectTitle.mapping = mapping;
	}

	/**
	 * Get a corresponding NotificationObjectTitle type, or undefined.
	 * @param {ObjectType} type
	 * @returns {NotificationObjectTitle<any>}
	 */
	static getObjectTitleForType(type: ObjectType): Type<NotificationObjectTitle<any>> {
		return NotificationObjectTitle.mapping.get(type);
	}

	public abstract handleClick(router: Router);
}

@Component({
	selector: 'chapter-group-notification-object',
	template: `Notifications about <a class="focus">{{object.name}}</a>`,
	styleUrls: ['notification-object.component.scss']
})
export class ChapterGroupNotificationObjectTitleComponent extends NotificationObjectTitle<ChapterGroup> {
	public handleClick(router: Router) {
		router.navigate(["/courses", this.object.chapterId, "editions", this.object.id, "groups", "all", "work"])
	}
}


@Component({
	selector: 'question-notification-object-title',
	template: `<span class="focus">Answers</span> to a question of you about <a class="focus">{{object.task.name}}</a>`,
	styles: []
})
export class QuestionNotificationObjectTitleComponent extends NotificationObjectTitle<QuestionDetailsDto> {
	public handleClick(router: Router) {
		router.navigate(["/dashboard/questions"])
	}
}

@Component({
	selector: 'submission-notification-object-title',
	template: `Notifications about your <a class="focus">submission</a> for <a class="focus">{{object.task.name}}</a>`,
	styles: []
})
export class SubmissionNotificationObjectTitleComponent extends NotificationObjectTitle<SubmissionDetails> {
	public handleClick(router: Router) {
		router.navigate([
			"/courses", this.object.task.chapterId,
			"editions", this.object.chapterGroupId,
			"submissions", this.object.id])
	}
}

@Component({
	selector: 'task-notification-object',
	template: `New question threads in <a class="focus">{{object.name}}</a>`,
	styles: []
})
export class TaskNotificationObjectTitleComponent extends NotificationObjectTitle<TaskEdition> {
	public handleClick(router: Router) {
		router.navigate(["/courses", this.object.chapterId, "editions", this.object.chapterGroupId, "tasks", this.object.id])
	}
}


NotificationObjectTitle.initialize();
