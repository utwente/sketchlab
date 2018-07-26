import {Component} from '@angular/core';
import {ChapterGroupEnrollNotificationEvent} from '../../../../_dto/notification';
import {NotificationEventComponent} from '../notification-event.component';

@Component({
	selector: 'chapter-group-enroll-event',
	templateUrl: './chapter-group-enroll-event.component.html',
	styleUrls: ['./chapter-group-enroll-event.component.scss']
})
export class ChapterGroupEnrollEventComponent extends NotificationEventComponent<ChapterGroupEnrollNotificationEvent> {

}
