import {TitleCasePipe} from '@angular/common';
import {Component, OnInit} from '@angular/core';
import {ListItem} from '../../_component/option-select/option-select.component';
import {ConfirmDialogComponent} from '../../_dialog/confirm-dialog/confirm-dialog.component';
import {User, UserRole, UserType} from '../../_dto/user';
import {DialogService} from '../../_service/dialog.service';
import {UserService} from '../../_service/user.service';
import {enumValues} from '../../utils/enum-values';

/**
 * Manages the roles of users, e.g. wether they are teachers or students.
 */
@Component({
	selector: 'role-management',
	templateUrl: './role-management.component.html',
	styleUrls: ['./role-management.component.scss'],
	providers: [TitleCasePipe]
})
export class RoleManagementComponent implements OnInit {
	/**
	 * The users to be shown in the table, matching the pattern in the search box.
	 */
	users: User[];

	/**
	 * The anonymous role.
	 * @type {UserRole.ANONYMOUS}
	 */
	anonymousRole: UserRole = UserRole.ANONYMOUS;

	/**
	 * All roles in the system, filters out the Anonymous role.
	 * @type {ListItem<string>[]}
	 */
	userRoles: ListItem<string>[] = Array.from(enumValues(UserRole))
		.filter(r => r !== this.anonymousRole)
		.map(r => new ListItem<string>(this.titleCasePipe.transform(r), r));

	constructor(private userService: UserService,
				private titleCasePipe: TitleCasePipe,
				private dialogService: DialogService) {
		//Pre-fill table with "all" users, limited to 100.
		this.findUsers();
	}

	ngOnInit() {
	}


	/**
	 * Finds users that correspond to the information given in the search form.
	 */
	findUsers() {
		this.userService.getUsers('', UserType.ALL, 100, true)
			.subscribe(users => this.users = users.filter(u => u.role != this.anonymousRole));
	}

	/**
	 * Updates the role of a user.
	 * @param {User} user
	 * @param {string} role
	 */
	updateUserRole(user: User, role: string) {
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			`Are you sure you want to make ${user.firstName} a ${role.toLowerCase()}?`
		).subscribe(confirm => {
			if (confirm) {
				this.userService.updateUserRole(user, <UserRole>role.toUpperCase())
					.subscribe(u => user = u);
			} //TODO: Fix two way binding for OptionSelectComponent.
		})
	}

	/**
	 * Binds the given users to the property.
	 * @param {User[]} users
	 */
	bindUsers(users: User[]) {
		this.users = users.filter(u => u.role != this.anonymousRole);
	}
}
