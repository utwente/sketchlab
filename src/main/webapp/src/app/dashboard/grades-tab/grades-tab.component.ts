import {Component, OnInit} from '@angular/core';
import {EnrollmentDetails} from '../../_dto/enrollment';
import {User, UserRole} from '../../_dto/user';
import {LoggedInUserService} from "../../_service/logged-in-user.service";
import {UserAuthenticationService} from '../../_service/user-authentication.service';

@Component({
	selector: 'app-grades-tab',
	templateUrl: './grades-tab.component.html',
	styleUrls: ['./grades-tab.component.scss']
})
export class GradesTabComponent implements OnInit {
	user: User;
	isTeacher: boolean;
	enrollments: EnrollmentDetails[];

	constructor(loggedInUserService: LoggedInUserService,
				userAuthenticationService: UserAuthenticationService) {
		userAuthenticationService.getCurrentUser().subscribe((user) => {
			this.user = user;
			this.isTeacher = user.role === UserRole.TEACHER;
		});

		loggedInUserService.getStudentEnrollments().subscribe((enrollments) => {
			this.enrollments = enrollments;
		});
	}

	ngOnInit() {
	}
}
