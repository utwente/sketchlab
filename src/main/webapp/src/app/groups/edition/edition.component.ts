import {Component, OnInit} from '@angular/core';
import {User, UserRole} from '../../_dto/user';
import {UserAuthenticationService} from '../../_service/user-authentication.service';

@Component({
	selector: 'app-edition',
	templateUrl: './edition.component.html',
	styleUrls: ['./edition.component.scss']
})
export class EditionComponent implements OnInit {
	loggedInUser: User;

	constructor(private userAuthenticationService: UserAuthenticationService) {
		this.userAuthenticationService.getCurrentUser().subscribe(user => this.loggedInUser = user);
	}

	ngOnInit() {
	}

	/**
	 * Returns true if the currently logged in user is a teacher.
	 * @returns {boolean}
	 */
	isTeacher(): boolean {
		return this.loggedInUser && this.loggedInUser.role == UserRole.TEACHER;
	}
}
