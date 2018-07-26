import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ErrorDialogComponent} from '../../_dialog/error-dialog/error-dialog.component';
import {PasswordResetDto} from '../../_dto/internal-user';
import {DialogService} from '../../_service/dialog.service';
import {PasswordResetService} from "../../_service/password-reset.service";
import {InfoDialogComponent} from "../../_dialog/info-dialog/info-dialog.component";

@Component({
	selector: 'app-password-reset',
	templateUrl: './password-reset.component.html',
})
export class PasswordResetComponent implements OnInit {
	/**
	 * The DTO in which to store the email address for the password reset.
	 * @type {PasswordResetDto}
	 */
	emailDto: PasswordResetDto = new PasswordResetDto();

	/**
	 * When the submit button has been pressed.
	 * @type {boolean}
	 */
	submitted: boolean = false;

	constructor(private passwordResetService: PasswordResetService,
				private router: Router,
				private dialogService: DialogService) {
	}

	ngOnInit() {
	}

	/**
	 * Resets the password. Will always be succesful so discovery attacks are not possible.
	 */
	resetPassword() {
		this.passwordResetService.resetPassword(this.emailDto)
			.subscribe(
				() => {
				},
				() => {
				},
				() => {
					InfoDialogComponent.create(
						this.dialogService,
						'Password reset',
						'A new password has been sent to the given email address, if it exists in our database.')
						.subscribe(() => {
							this.router.navigateByUrl('/account/login')
						});
				})
	}
}
