import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ChangePasswordDto} from '../../_dto/internal-user';
import {UserAuthenticationService} from "../../_service/user-authentication.service";
import {MultiFieldValidator} from '../../_validator/MultiFieldValidator';

@Component({
	selector: 'app-password-change',
	templateUrl: './password-change.component.html',
	styleUrls: ['./password-change.component.scss']
})
export class PasswordChangeComponent implements OnInit {
	passwordForm: FormGroup;
	wrongPassword: boolean = false;
	loading: boolean = false;
	success: boolean = false;

	constructor(private userAuthenticationService: UserAuthenticationService, formBuilder: FormBuilder) {

		this.passwordForm = formBuilder.group({
			oldPassword: ['', [Validators.required]],
			newPassword: ['', [Validators.minLength(8), Validators.required]],
			newPasswordVerify: ''
		}, {
			validator: MultiFieldValidator.validate(
				'newPassword',
				'newPasswordVerify',
				(password, passwordVerify) => password === passwordVerify
			)
		});
	}


	/**
	 * Changes the users password.
	 */
	changePassword() {
		this.wrongPassword = false;
		this.loading = true;
		this.success = false;

		const form = this.passwordForm;

		if (form.valid) {
			const dto: ChangePasswordDto = {
				oldPassword: form.get('oldPassword').value,
				newPassword: form.get('newPassword').value
			};
			this.userAuthenticationService.changePassword(dto).subscribe(
				() => {
					this.loading = false;
					this.success = true;
					form.reset();
				},
				error => {
					form.get('newPasswordVerify').reset();
					this.loading = false;
					this.success = false;
					console.log(error);
					switch (error.status) {
						case 403:
							this.wrongPassword = true;
							break;
						case 400:
							form.get('newPassword').setErrors({'minlength': true});
							break;
						default:
							throw error;
					}
				}
			);
		}
	}

	ngOnInit() {
	}
}
