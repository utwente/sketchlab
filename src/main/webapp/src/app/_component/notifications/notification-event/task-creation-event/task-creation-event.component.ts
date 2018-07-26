import {Component} from '@angular/core';
import {NotificationEventComponent} from "../notification-event.component";
import {TaskCreationNotificationEvent} from "../../../../_dto/notification";

@Component({
	selector: 'task-creation-event',
	templateUrl: './task-creation-event.component.html',
	styleUrls: ['./task-creation-event.component.scss']
})
export class TaskCreationEventComponent extends NotificationEventComponent<TaskCreationNotificationEvent> {
}
