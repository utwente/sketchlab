import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppConfig} from "../app.config";
import {Observable} from "rxjs/Observable";
import {NotificationDto} from "../_dto/notification";

@Injectable()
export class NotificationService {
	constructor(private http: HttpClient, private config: AppConfig) {
	}

	/**
	 * Retrieve all notifications for the current user.
	 * @returns {Observable<NotificationDto[]>}
	 */
	public getAll(): Observable<NotificationDto[]> {
		return this.http.get<NotificationDto[]>(`${this.config.apiUrl}/notifications`);
	}

	/**
	 * Delete a particular notification.
	 * @param {number} notificationId
	 * @returns {Observable<void>}
	 */
	public delete(notificationId: number): Observable<void> {
		return this.http.delete<void>(`${this.config.apiUrl}/notifications/${notificationId}`);
	}
}
