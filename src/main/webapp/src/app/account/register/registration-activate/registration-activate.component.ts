import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {ActivateUserDto} from '../../../_dto/internal-user';
import {UserRegistrationService} from '../../../_service/user-registration.service';

@Component({
	selector: 'app-registration-activate',
	templateUrl: './registration-activate.component.html',
	styleUrls: ['./registration-activate.component.scss']
})
export class RegistrationActivateComponent implements OnInit {
	/**
	 * True when the loading spinner should be present.
	 * @type {boolean}
	 */
	loading: boolean = true;

	/**
	 * When the activation process was succesful.
	 * @type {boolean}
	 */
	activationSucces: boolean = false;

	constructor(private internalUserService: UserRegistrationService, private route: ActivatedRoute) {
	}

	/**
	 * Try activating the account by retrieving the necessary information from the query parameters.
	 */
	ngOnInit() {
		const activationDto = new ActivateUserDto();
		activationDto.userId = this.route.snapshot.queryParams['user'];
		activationDto.token = this.route.snapshot.queryParams['token'];

		this.internalUserService.activate(activationDto).subscribe(
			() => {
				setTimeout(() => {
					this.loading = false;
					this.activationSucces = true;
				}, 500);
			},
			() => {
				setTimeout(() => {
					this.loading = false;
					this.activationSucces = false;
				}, 500);
			}
		)
	}

}
