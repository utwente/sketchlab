import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {ReplaySubject} from 'rxjs/ReplaySubject';
import {ChangePasswordDto, InternalUserLoginDto} from '../_dto/internal-user';
import {InternalUser, User} from '../_dto/user';
import {AppConfig} from '../app.config';

/**
 * Provides the currently logged-in user.
 */
@Injectable()
export class UserAuthenticationService {
	private currentUser: ReplaySubject<User> = new ReplaySubject<User>(1);

	constructor(private http: HttpClient, private config: AppConfig) {
		this.refresh()
	}

	/**
	 * Do an API call to get the current user, and update the {@link getCurrentUser} observable with the newly
	 * retrieved user.
	 */
	public refresh() {
		this.getMe().subscribe(this.currentUser.next.bind(this.currentUser));
	}

	/**
	 * Get the current user. This is a cached value and will not make an API call.
	 * @returns {Observable<User>}
	 */
	public getCurrentUser(): Observable<User> {
		return this.currentUser.asObservable();
	}

	/**
	 * Do an API call to get the current user. Only use this if you know that the cached user in currentUser is
	 * outdated.
	 * @returns {Observable<User>}
	 */
	private getMe(): Observable<User> {
		return this.http.get<User>(`${this.config.apiUrl}/users/me`);
	}

	/**
	 * Log in as an internal user.
	 * @param {InternalUserLoginDto} loginDto The login DTO to send to the API
	 * @returns {Observable<User>} When logged in, will return the new user. Do not use this object,
	 * use <code>refresh()</code> and <code>getCurrentUser()</code> instead.
	 */
	public internalUserLogin(loginDto: InternalUserLoginDto): Observable<User> {
		return this.http.put<User>(`${this.config.apiUrl}/users/me`, loginDto);
	}

	/**
	 * Logs out an internal user.
	 * @returns {Observable<void>} Nothing on success, the reasons why on error.
	 */
	private internalUserLogout() {
		return this.http.delete<void>(`${this.config.apiUrl}/users/me`);
	}


	/**
	 * Changes password of the currently logged in user.
	 * @param {ChangePasswordDto} changePasswordDto
	 * @returns {Observable<InternalUser>}
	 */
	changePassword(changePasswordDto: ChangePasswordDto): Observable<InternalUser> {
		return this.http.put<InternalUser>(
			`${this.config.apiUrl}/users/me/change-password`,
			changePasswordDto
		);
	}

	/**
	 * Returns the URL at which a UT user may log out.
	 * @returns {string} The URL to redirect to for a SSO logout
	 */
	public getSsoLogoutUrl(): string {
		return `${this.config.hostUrl}/sso/logout?redirect-url=${this.config.hostUrl}`;
	}

	/**
	 * Logs out the current user. Redirects the page on complete.
	 */
	public logoutCurrentUser() {
		this.getCurrentUser().subscribe(user => {
			// Check if user's friendly ID adheres to a UT user ID, if so, use the SSO way of logging
			// out.
			if (/^[a-z][0-9]{7}$/i.test(user.friendlyId)) {
				window.location.href = this.getSsoLogoutUrl();
			} else {
				this.internalUserLogout().subscribe(
					() => { //Do nothing on success.
					},
					() => { //Do nothing on error.
					},
					() => window.location.href = this.config.hostUrl //Always reload.
				);
			}
		})

	}
}
