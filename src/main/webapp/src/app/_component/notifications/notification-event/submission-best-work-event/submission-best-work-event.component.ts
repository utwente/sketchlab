import {Component} from '@angular/core';
import {NotificationEventComponent} from "../notification-event.component";
import {SubmissionBestWorkNotificationEvent} from "../../../../_dto/notification";

@Component({
	selector: 'submission-best-work-event',
	templateUrl: './submission-best-work-event.component.html',
	styleUrls: ['./submission-best-work-event.component.scss']
})
export class SubmissionBestWorkEventComponent extends NotificationEventComponent<SubmissionBestWorkNotificationEvent> {
}
