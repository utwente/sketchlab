import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {InternalUserRegistrationDto} from '../../_dto/internal-user';
import {User} from '../../_dto/user';
import {UserRegistrationService} from '../../_service/user-registration.service';
import {MultiFieldValidator} from '../../_validator/MultiFieldValidator';

@Component({
	selector: 'app-register',
	templateUrl: './register.component.html',
	styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
	/**
	 * Represents the form
	 */
	registerForm: FormGroup;

	/**
	 * Field which tracks whether the user has used the submit button. When true, will show the
	 * loading spinner. Otherwise will show the form.
	 * @type {boolean}
	 */
	submitted: boolean = false;

	/**
	 * True when the registration was successful, false if otherwise.
	 * @type {boolean}
	 */
	registrationSuccess: boolean = false;

	/**
	 * The user which was just registered.
	 */
	registeredUser: User;

	/**
	 * When the registration process has encountered an error.
	 * @type {boolean}
	 */
	hasError: boolean = false;

	/**
	 * The message which was returned by the back end.
	 */
	errorMessage: string;

	constructor(fb: FormBuilder, private internalUserService: UserRegistrationService) {
		this.buildForm(fb);
	}

	ngOnInit() {

	}

	/**
	 * Builds the form for the registration process.
	 * @param {FormBuilder} fb The FormBuilder to use.
	 */
	private buildForm(fb: FormBuilder) {
		this.registerForm = fb.group({
			firstName: ['', Validators.required],
			lastName: '',
			email: ['', [Validators.required, Validators.email]],
			emailVerify: '',
			password: ['', [Validators.required, Validators.minLength(8)]],
			passwordVerify: '',
			accepttoc: [false, Validators.requiredTrue]
		}, {
			validator: [
				MultiFieldValidator.validate(
					'email',
					'emailVerify',
					(email, emailVerify) => email === emailVerify
				),
				MultiFieldValidator.validate(
					'password',
					'passwordVerify',
					(password, passwordVerify) => password === passwordVerify
				)
			]
		});
	}

	/**
	 * Register the account for which the credentials are given.
	 */
	register() {
		this.submitted = true;
		this.registrationSuccess = false;
		this.hasError = false;

		const form = this.registerForm;
		const internalUserDto = new InternalUserRegistrationDto();
		internalUserDto.firstName = form.get('firstName').value;
		internalUserDto.lastName = form.get('lastName').value;
		internalUserDto.email = form.get('email').value;
		internalUserDto.password = form.get('password').value;

		this.internalUserService.register(internalUserDto).subscribe(
			data => {
				this.registeredUser = data;
				this.registrationSuccess = true;

				setTimeout(() => {
					this.submitted = false;
				}, 500);
			},
			error => {
				this.hasError = true;
				switch (error.responseStatus) {
					case 409:
						this.errorMessage = 'The given email address is already in use.';
						break;
					case 400:
						this.errorMessage = error.message;
						break;
					default:
						this.errorMessage = 'An error has occured, please try again later.';
						break;
				}

				setTimeout(() => {
					this.submitted = false;
				}, 500);
			}
		);
	}
}
