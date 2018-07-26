import {
	Component,
	ComponentFactory,
	ComponentFactoryResolver,
	ComponentRef,
	Input,
	OnInit,
	Type,
	ViewChild,
	ViewContainerRef
} from '@angular/core';
import {NotificationEventComponent} from "../notification-event/notification-event.component";
import {TaskCreationEventComponent} from "../notification-event/task-creation-event/task-creation-event.component";
import {SubmissionAnnotationEventComponent} from "../notification-event/submission-annotation-event/submission-annotation-event.component";
import {ChapterGroupEnrollEventComponent} from "../notification-event/chapter-group-enroll-event/chapter-group-enroll-event.component";
import {NotificationEvent, NotificationType} from "../../../_dto/notification";
import {QuestionAnswerEventComponent} from "../notification-event/question-answer-event/question-answer-event.component";
import {ChapterGroupGradeEventComponent} from "../notification-event/chapter-group-grade-event/chapter-group-grade-event.component";
import {SubmissionBestWorkEventComponent} from "../notification-event/submission-best-work-event/submission-best-work-event.component";
import {TaskQuestionEventComponent} from "../notification-event/task-question-event/task-question-event.component";

@Component({
	selector: 'notification-event-container',
	templateUrl: './notification-event-container.component.html',
	styleUrls: ['./notification-event-container.component.scss']
})
export class NotificationEventContainerComponent implements OnInit {

	@Input("event")
	event: NotificationEvent;

	// notifications will be created in the DOM after this
	@ViewChild('insertionPoint', {read: ViewContainerRef})
	insertionPoint: ViewContainerRef;

	private mapping: Map<NotificationType, Type<NotificationEventComponent<any>>>;

	constructor(private componentFactoryResolver: ComponentFactoryResolver) {
		const mapping: Map<NotificationType, any> = new Map();

		mapping.set(NotificationType.CHAPTER_GROUP_ENROLL, ChapterGroupEnrollEventComponent);
		mapping.set(NotificationType.CHAPTER_GROUP_GRADE, ChapterGroupGradeEventComponent);
		mapping.set(NotificationType.QUESTION_ANSWER, QuestionAnswerEventComponent);
		mapping.set(NotificationType.SUBMISSION_ANNOTATION, SubmissionAnnotationEventComponent);
		mapping.set(NotificationType.SUBMISSION_BEST_WORK, SubmissionBestWorkEventComponent);
		mapping.set(NotificationType.TASK_CREATION, TaskCreationEventComponent);
		mapping.set(NotificationType.TASK_QUESTION, TaskQuestionEventComponent);

		this.mapping = mapping;
	}

	ngOnInit(): void {
		// Load the child
		const objectType: Type<NotificationEventComponent<any>>
			= this.mapping.get(this.event.notificationType);

		if (objectType === undefined) {
			console.error(`Unrecognized notification event type: ${this.event.notificationType}`);
			return;
		}

		const factory: ComponentFactory<NotificationEventComponent<any>>
			= this.componentFactoryResolver.resolveComponentFactory(objectType);
		const component: ComponentRef<NotificationEventComponent<any>>
			= this.insertionPoint.createComponent(factory);
		component.instance.notificationEvent = this.event;
	}

}
