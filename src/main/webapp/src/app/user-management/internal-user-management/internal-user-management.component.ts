import {Component, OnInit} from '@angular/core';
import {InternalUserDetails, InternalUserUpdateDto} from "../../_dto/internal-user";
import {InternalUserService} from "../../_service/internal-user.service";
import {ConfirmDialogComponent} from "../../_dialog/confirm-dialog/confirm-dialog.component";
import {DialogService} from "../../_service/dialog.service";

@Component({
	selector: 'app-internal-user-management',
	templateUrl: './internal-user-management.component.html',
	styleUrls: ['./internal-user-management.component.scss']
})
export class InternalUserManagementComponent implements OnInit {
	users: InternalUserDetails[];

	searchInput: string = '';

	constructor(
		private dialogService: DialogService,
		private internalUserService: InternalUserService) {
	}

	ngOnInit() {
		this.findUsers();
	}

	findUsers() {
		this.internalUserService.findUsers(this.searchInput).subscribe(us => this.users = us);
	}

	/**
	 * Toggles the active state of the given user, regardless of the email sent. When inactive,
	 * the user is unable to log in.
	 * @param {InternalUserDetails} user
	 * @param {HTMLInputElement} checkbox
	 */
	toggleActive(user: InternalUserDetails, checkbox: HTMLInputElement) {
		const opString = user.active ? 'deactivate' : 'activate';
		const name = user.user.firstName + (user.user.lastName ? ' ' + user.user.lastName : '');
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			`Are you sure you want to ${opString} user ${name}?`
		).subscribe(confirm => {
			if (confirm) {
				const dto: InternalUserUpdateDto = Object.assign({}, user.user);
				dto.active = !user.active;
				this.updateUser(user.userId, dto);
			} else {
				checkbox.checked = user.active;
			}
		});
	}

	/**
	 * Toggles the suspension state of the given user. When suspended, the user is unable to log in.
	 * @param {InternalUserDetails} user
	 * @param {HTMLInputElement} checkbox
	 */
	toggleSuspended(user: InternalUserDetails, checkbox: HTMLInputElement) {
		const opString = user.suspended ? 'unsuspend' : 'suspend';
		const name = user.user.firstName + (user.user.lastName ? ' ' + user.user.lastName : '');
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			`Are you sure you want to ${opString} user ${name}?`
		).subscribe(confirm => {
			if (confirm) {
				const dto: InternalUserUpdateDto = Object.assign({}, user.user);
				dto.suspended = !user.suspended;
				this.updateUser(user.userId, dto);
			} else {
				checkbox.checked = user.suspended;
			}
		});
	}

	/**
	 * Updates the given userId with the given dto information.
	 * @param {string} userId
	 * @param {InternalUserUpdateDto} dto
	 */
	private updateUser(userId: string, dto: InternalUserUpdateDto) {
		this.internalUserService.updateUser(userId, dto).subscribe(() => this.findUsers());
	}
}
