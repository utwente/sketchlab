import {Component, Input, OnChanges, OnInit} from '@angular/core';
import {User, UserRole} from '../../_dto/user';
import {EditAvatarDialogComponent} from "./edit-avatar-dialog/edit-avatar-dialog.component";
import {DialogService} from "../../_service/dialog.service";
import {SafeUrl} from "@angular/platform-browser";
import {UserAvatarService} from "../../_service/user-avatar.service";
import {Md5} from "ts-md5";
import {UserAuthenticationService} from "../../_service/user-authentication.service";

@Component({
	selector: 'avatar',
	templateUrl: './avatar.component.html',
	styleUrls: ['./avatar.component.scss']
})
export class AvatarComponent implements OnInit, OnChanges {
	private readonly INITIAL_LIMIT = 2;

	@Input('user') public user: User;
	@Input('size') public size: number;
	@Input('editable') public editable: boolean = false;

	loggedInUser: User;
	avatar: SafeUrl;
	containerStyle: {};
	hover: boolean = false;

	constructor(
		private userAuthenticationService: UserAuthenticationService,
		private avatarService: UserAvatarService,
		private dialogService: DialogService) {
	}

	ngOnInit() {
		this.userAuthenticationService.getCurrentUser().subscribe(u => this.loggedInUser = u);
	}

	ngOnChanges() {
		if (this.user && this.size) {
			this.getAvatar();

			this.containerStyle = {
				'width': this.size + 'px',
				'height': this.size + 'px',
				'font-size': (this.size / 2) + 'px',
				'background-color': `hsl(${this.hueFromId}, 77%, 56%)`
			};
		}
	}

	/**
	 * Retrieves the avatar of the given user, if not available set it to null so we can show
	 * the user's initials.
	 */
	getAvatar() {
		this.avatarService.getAvatar(this.user.id)
			.subscribe(
				avatar => {
					this.avatar = avatar;
				},
				() => {
					this.avatar = null
				}
			)
		;
	}

	/**
	 * Returns true if editing avatars is allowed, which is the case when the user is a teacher or
	 * the logged in user is the same as the avatar's user. The logged in user may not be anonymous.
	 * @returns {boolean}
	 */
	get editAllowed(): boolean {
		if (!this.loggedInUser || this.loggedInUser.role == UserRole.ANONYMOUS) {
			return false;
		}
		return this.loggedInUser.role == UserRole.TEACHER || this.loggedInUser.id == this.user.id;
	}

	/**
	 * Tries to determine the user's initials.
	 * @returns {string}
	 */
	get userInitials(): string {
		const names = [...this.user.firstName.split(' ')];
		if (this.user.lastName) {
			names.push(...this.user.lastName.split(' '));
		}
		const initials = names
			.map(name => name[0])
			.filter(c => c === c.toUpperCase())
			.slice(0, Math.min(names.length, this.INITIAL_LIMIT))
			.join('');

		return initials.length != 0 ? initials : names[0].charAt(0);
	}

	/**
	 * Determines a hue shade from the user's ID by using a top secret hashing algorithm.
	 * @returns {number}
	 */
	private get hueFromId() {
		return (<Int32Array>Md5.hashStr(this.user.id, true)).reduce((p, c) => p + c % 360, 0);
	}

	/**
	 * Shows the edit avatar dialog.
	 */
	public editAvatar() {
		EditAvatarDialogComponent.create(
			this.dialogService,
			this.user,
			!!this.avatar,
			() => this.getAvatar()
		);
	}
}
