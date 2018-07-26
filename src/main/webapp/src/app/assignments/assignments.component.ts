import {Component, OnInit} from '@angular/core';
import {UserRole} from "../_dto/user";
import {UserAuthenticationService} from "../_service/user-authentication.service";

@Component({
	selector: 'app-assignments',
	templateUrl: './assignments.component.html',
	styleUrls: ['./assignments.component.scss']
})
export class AssignmentsComponent implements OnInit {

	public role: UserRole;

	constructor(authenticationService: UserAuthenticationService,) {
		authenticationService.getCurrentUser().subscribe(user => this.role = user.role);
	}

	ngOnInit() {
	}

}
