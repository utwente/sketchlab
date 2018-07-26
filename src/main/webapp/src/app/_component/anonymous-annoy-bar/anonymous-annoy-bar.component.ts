import {Component, Input, OnInit} from '@angular/core';
import {User, UserRole} from '../../_dto/user';
import {UserAuthenticationService} from '../../_service/user-authentication.service';

@Component({
	selector: 'anonymous-annoy-bar',
	templateUrl: './anonymous-annoy-bar.component.html',
	styleUrls: ['./anonymous-annoy-bar.component.scss']
})
export class AnonymousAnnoyBarComponent implements OnInit {
	@Input() hideAnnoyBar: boolean = false;

	user: User;

	constructor(private userAuthenticationService: UserAuthenticationService) {
		userAuthenticationService.getCurrentUser().subscribe(user => this.user = user);
	}

	ngOnInit() {
	}

	isAnonymous(): boolean {
		return this.user && this.user.role == UserRole.ANONYMOUS;
	}
}
