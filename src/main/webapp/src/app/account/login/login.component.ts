import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {ErrorDialogComponent} from '../../_dialog/error-dialog/error-dialog.component';
import {InternalUserLoginDto} from '../../_dto/internal-user';
import {DialogService} from '../../_service/dialog.service';
import {UserAuthenticationService} from '../../_service/user-authentication.service';

@Component({
	selector: 'app-login',
	templateUrl: './login.component.html',
})
export class LoginComponent implements OnInit {
	/**
	 * The DTO in which to store the login information.
	 * @type {InternalUserLoginDto}
	 */
	loginDto: InternalUserLoginDto = new InternalUserLoginDto();

	/**
	 * Whether the login button has been pressed, when true, will show the loading spinner.
	 * @type {boolean}
	 */
	loggingIn: boolean = false;

	/**
	 * When the given credentials happen to be incorrect.
	 * @type {boolean}
	 */
	invalidCredentials: boolean = false;

	constructor(
		private authenticationService: UserAuthenticationService,
		private router: Router,
		private dialogService: DialogService
	) {
	}

	ngOnInit() {
	}

	/**
	 * Logs in the user. When succesful will redirect to the user's dashboard, otherwise will show
	 * a nice error message.
	 */
	login() {
		this.loggingIn = true;
		this.authenticationService.internalUserLogin(this.loginDto)
			.subscribe(
				() => {
					this.authenticationService.refresh();
					setTimeout(() => this.router.navigateByUrl('/dashboard'), 500)
				},
				error => {
					switch (error.status) {
						case 403:
						case 404:
							setTimeout(() => {
								this.invalidCredentials = true;
								this.loggingIn = false;
								this.loginDto = new InternalUserLoginDto();
							}, 500);
							break;
						default:
							//Something is terribly wrong, what to do now?
							ErrorDialogComponent.create(this.dialogService, error.toString()).subscribe(() => {
								this.loggingIn = false;
								this.loginDto = new InternalUserLoginDto();
							});
							break;

					}
				}
			)
	}
}
