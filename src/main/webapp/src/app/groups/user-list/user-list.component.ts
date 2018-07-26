import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ListItem} from '../../_component/option-select/option-select.component';
import {User, UserEnrollment, UserRole} from '../../_dto/user';
import {UserAuthenticationService} from '../../_service/user-authentication.service';

@Component({
	selector: 'user-list',
	templateUrl: './user-list.component.html',
	styleUrls: ['./user-list.component.scss']
})
export class UserListComponent implements OnInit {

	@Input() users: UserEnrollment[];
	@Input() chapterId: number;
	@Input() chapterGroupId: number;
	@Input() allowTaEditing: boolean = false;
	@Input() editIcon: string = "pencil";

	@Output() onEdit: EventEmitter<UserEnrollment> = new EventEmitter<UserEnrollment>();
	@Output() onDelete: EventEmitter<UserEnrollment> = new EventEmitter<UserEnrollment>();

	loggedInUser: User;
	/**
	 * Creates sort options
	 * @type {ListItem<(u1, u2) => any>[]}
	 */
	sortOptions: ListItem<(u1: UserEnrollment, u2: UserEnrollment) => number>[] = [
		new ListItem(
			'By role',
			(u1, u2) => {
				const a1 = u1.enrollment.assistant;
				const a2 = u2.enrollment.assistant;
				return a1 == a2 ? u1.firstName.localeCompare(u2.firstName) : a1 && !a2 ? -1 : 1;
			}),
		new ListItem('Alphabetical', (u1, u2) => u1.firstName.localeCompare(u2.firstName))
	];

	constructor(
		private authenticationService: UserAuthenticationService
	) {
	}

	ngOnInit() {
		this.authenticationService.getCurrentUser().subscribe(u => this.loggedInUser = u);
	}

	/**
	 * Sorts users by the given function.
	 * @param {(u1: UserEnrollment, u2: UserEnrollment) => number} fn
	 */
	sortUsers(fn: (u1: UserEnrollment, u2: UserEnrollment) => number) {
		this.users.sort(fn);
	}

	/**
	 * Creates a portfolio URL for the given user.
	 * @param {UserEnrollment} user
	 * @returns {Array<string | number>}
	 */
	createUrl(user: UserEnrollment): Array<string | number> {
		return ['/courses', this.chapterId, 'editions', this.chapterGroupId, 'users', user.id];
	}

	// noinspection JSMethodCanBeStatic
	/**
	 * Returns whether the given user is a TA in the given chapter group.
	 * @param {UserEnrollment} user
	 * @returns {boolean}
	 */
	isTa(user: UserEnrollment): boolean {
		return user && user.enrollment.assistant;
	}

	/**
	 * Returns whether the currently logged in user is a teacher.
	 * @returns {boolean}
	 */
	isTeacher(): boolean {
		return this.loggedInUser && this.loggedInUser.role == UserRole.TEACHER;
	}

	/**
	 * Triggers onEdit event.
	 * @param {UserEnrollment} user
	 */
	clickEditEnrollment(user: UserEnrollment) {
		this.onEdit.emit(user);
	}

	/**
	 * Triggers onDelete event.
	 * @param {UserEnrollment} user
	 */
	clickDeleteEnrollment(user: UserEnrollment) {
		this.onDelete.emit(user);
	}
}
