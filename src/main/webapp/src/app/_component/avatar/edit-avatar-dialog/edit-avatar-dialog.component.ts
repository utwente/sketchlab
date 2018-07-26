import {Component} from '@angular/core';
import {DialogBaseComponent} from "../../dialog-base/dialog-base.component";
import {UserAvatarService} from "../../../_service/user-avatar.service";
import {Dialog, DialogService} from "../../../_service/dialog.service";
import {User} from "../../../_dto/user";
import {ConfirmDialogComponent} from "../../../_dialog/confirm-dialog/confirm-dialog.component";

@Component({
	selector: 'app-edit-avatar-dialog',
	templateUrl: './edit-avatar-dialog.component.html',
	styleUrls: []
})
export class EditAvatarDialogComponent extends DialogBaseComponent<EditAvatarDialogComponent, void> {
	userId: string;

	avatarAvailable: boolean;

	updateCallback: () => void;

	constructor(
		private avatarService: UserAvatarService,
		private dialogService: DialogService
	) {
		super();
	}

	/**
	 * Returns the URL where avatars can be uploaded to.
	 * @returns {string}
	 */
	get avatarUploadUrl(): string {
		return this.avatarService.getAvatarUrl(this.userId);
	}

	/**
	 * Checks if an avatar is available for the given user.
	 */
	loadAvatar() {
		this.avatarService.getAvatar(this.userId).subscribe(() => {
			this.avatarAvailable = true;
		}, () => {
			this.avatarAvailable = false;
		}, () => {
			this.updateCallback();
		});
	}

	/**
	 * Deletes an avatar for the given user.
	 */
	deleteAvatar() {
		ConfirmDialogComponent.create(
			this.dialogService,
			'Please confirm',
			'Are you sure you want to delete this avatar?'
		).subscribe(confirm => {
			if (confirm) {
				this.avatarService.deleteAvatar(this.userId).subscribe(() => {
					this.avatarAvailable = false;
					this.updateCallback();
				});
			}
		});
	}

	/**
	 * Closes the dialog
	 */
	closeDialog() {
		this.dialog.close(null);
	}

	/**
	 * Creates a new edit-avatar dialog where a new avatar can be uploaded to or an existing one
	 * can be deleted.
	 * @param {DialogService} dialogService
	 * @param {User} user
	 * @param {boolean} avatarAvailable
	 * @param {() => void} updateCallback
	 */
	static create(
		dialogService: DialogService,
		user: User,
		avatarAvailable: boolean,
		updateCallback: () => void
	) {
		const dialog: Dialog<EditAvatarDialogComponent, void> = dialogService
			.open(EditAvatarDialogComponent);
		dialog.instance.userId = user.id;
		dialog.instance.avatarAvailable = avatarAvailable;
		dialog.instance.updateCallback = updateCallback;
		dialog.afterClose();
	}
}
