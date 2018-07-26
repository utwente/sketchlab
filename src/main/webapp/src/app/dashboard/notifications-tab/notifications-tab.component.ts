import {Component, OnInit} from '@angular/core';
import {NotificationService} from "../../_service/notification.service";
import {NotificationDto, NotificationEvent} from "../../_dto/notification";

@Component({
	selector: 'app-notifications-tab',
	templateUrl: './notifications-tab.component.html',
	styleUrls: ['./notifications-tab.component.scss']
})
export class NotificationsTabComponent implements OnInit {
	notifications: NotificationDto[];

	constructor(private notificationService: NotificationService) {
		notificationService.getAll().subscribe(notifications => this.notifications = notifications);
	}

	ngOnInit() {
	}

	public onDelete(event: NotificationEvent): void {
		this.notificationService.delete(event.id).subscribe(() => {
			let updatedNotifications: NotificationDto[] = [];
			for (let notification of this.notifications) {
				notification.notifications = notification.notifications.filter((value, index) => value.id != event.id);
				if (notification.notifications.length > 0) {
					// Only push when there are events. Otherwise remove the whole thing
					updatedNotifications.push(notification);
				}
			}
			this.notifications = updatedNotifications;
		});
	}
}
