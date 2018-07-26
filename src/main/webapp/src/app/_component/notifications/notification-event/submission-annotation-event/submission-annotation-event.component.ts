import {Component} from '@angular/core';
import {NotificationEventComponent} from "../notification-event.component";
import {SubmissionAnswerNotificationEvent} from "../../../../_dto/notification";

@Component({
	selector: 'submission-annotation-event',
	templateUrl: './submission-annotation-event.component.html',
	styleUrls: ['./submission-annotation-event.component.scss']
})
export class SubmissionAnnotationEventComponent extends NotificationEventComponent<SubmissionAnswerNotificationEvent> {

}
