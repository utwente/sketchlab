import {Component} from '@angular/core';
import {NotificationEventComponent} from "../notification-event.component";
import {QuestionAnswerNotificationEvent} from "../../../../_dto/notification";

@Component({
	selector: 'question-answer-event',
	templateUrl: './question-answer-event.component.html',
	styleUrls: ['./question-answer-event.component.scss']
})
export class QuestionAnswerEventComponent extends NotificationEventComponent<QuestionAnswerNotificationEvent> {


}
