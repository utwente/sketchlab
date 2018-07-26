import {Component, OnInit} from '@angular/core';
import {User, UserRole} from '../_dto/user';
import {DialogService} from '../_service/dialog.service';
import {UserAuthenticationService} from '../_service/user-authentication.service';

@Component({
	selector: 'app-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

	public user: User;

	constructor(private authenticationService: UserAuthenticationService) {
		authenticationService.getCurrentUser().subscribe(user => this.user = user);
	}

	ngOnInit() {
	}

	/**
	 * Returns true if the user is a student (or the anonymous user, who views the app as a student).
	 * @returns {boolean}
	 */
	public isStudent(): boolean {
		return this.user && (this.user.role == UserRole.STUDENT || this.user.role == UserRole.ANONYMOUS);
	}

	/**
	 * Returns true if the user is present and anonymous.
	 * @returns {boolean}
	 */
	public isAnonymous(): boolean {
		return this.user && this.user.role == UserRole.ANONYMOUS;
	}

	/**
	 * Returns true if the user is present, not anonymous and not a UT user.
	 * @returns {boolean}
	 */
	public isInternal() {
		return !this.isAnonymous() &&
			this.user && this.user.friendlyId && !/^[a-z][0-9]{7}$/i.test(this.user.friendlyId)
	}

	/**
	 * Logs out the current user. Note that this causes a page reload/redirect.
	 */
	public logout() {
		this.authenticationService.logoutCurrentUser();
	}
}
