import {Component} from '@angular/core';
import {NotificationEventComponent} from "../notification-event.component";
import {TaskQuestionNotificationEvent} from "../../../../_dto/notification";

@Component({
	selector: 'task-question-event',
	templateUrl: './task-question-event.component.html',
	styleUrls: ['./task-question-event.component.scss']
})
export class TaskQuestionEventComponent extends NotificationEventComponent<TaskQuestionNotificationEvent> {
}
