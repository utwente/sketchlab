import {OnInit} from '@angular/core';
import {NotificationEvent} from "../../../_dto/notification";

/**
 * The {@link NotificationEventComponent} is the base class for all notification event components.
 * These components map to a single event within a notification event list.
 */
export class NotificationEventComponent<N extends NotificationEvent> implements OnInit {
	public notificationEvent: N;

	constructor() {

	}

	ngOnInit() {

	}

}
