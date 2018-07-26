import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AppConfig} from "../app.config";
import {DomSanitizer, SafeUrl} from "@angular/platform-browser";
import {Observable} from "rxjs/Observable";

@Injectable()
export class UserAvatarService {

	constructor(
		private http: HttpClient,
		private config: AppConfig,
		private domSanitizer: DomSanitizer
	) {
	}

	/**
	 * Returns the URL where avatars can be found, updated or deleted.
	 * @param {string} userId
	 * @returns {string}
	 */
	getAvatarUrl(userId: string): string {
		return `${this.config.apiUrl}/users/${userId}/avatar`;
	}

	/**
	 * Returns the given user's avatar as a data URL.
	 * @param {string} userId
	 * @returns {Observable<SafeUrl>}
	 */
	getAvatar(userId: string): Observable<SafeUrl> {
		return this.http.get(this.getAvatarUrl(userId), {responseType: 'blob'})
			.map(e => this.domSanitizer.bypassSecurityTrustUrl(URL.createObjectURL(e)));
	}

	/**
	 * Deletes an avatar.
	 * @param {string} userId
	 * @returns {Observable<void>}
	 */
	deleteAvatar(userId: string) {
		return this.http.delete<void>(this.getAvatarUrl(userId));
	}
}
