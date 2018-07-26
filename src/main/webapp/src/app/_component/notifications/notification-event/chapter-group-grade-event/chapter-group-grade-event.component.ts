import {Component} from '@angular/core';
import {ChapterGroupGradeNotificationEvent} from '../../../../_dto/notification';
import {NotificationEventComponent} from '../notification-event.component';

@Component({
	selector: 'chapter-group-grade-event',
	templateUrl: './chapter-group-grade-event.component.html',
	styleUrls: ['./chapter-group-grade-event.component.scss']
})
export class ChapterGroupGradeEventComponent extends NotificationEventComponent<ChapterGroupGradeNotificationEvent> {
}
