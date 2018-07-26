import {Component, OnInit} from '@angular/core';
import {DialogService} from "../../_service/dialog.service";
import {ChapterGroupService} from "../../_service/chapter-group.service";
import {ChapterGroup, ChapterWithChapterGroupsAndSubgroups} from "../../_dto/chapter";
import {ChapterSubgroupService} from "../../_service/chapter-subgroup.service";
import {EnrollmentDetails} from "../../_dto/enrollment";
import {LoggedInUserService} from "../../_service/logged-in-user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {UserAuthenticationService} from "../../_service/user-authentication.service";
import {User} from "../../_dto/user";

@Component({
	selector: 'app-groups-landing-page',
	templateUrl: './groups-landing-page.component.html',
	styleUrls: ['./groups-landing-page.component.scss']
})
export class GroupsLandingPageComponent implements OnInit {
	chapterGroups: ChapterWithChapterGroupsAndSubgroups[];
	loggedInUser: User;

	enrollments: EnrollmentDetails[];

	active: ChapterGroup;

	constructor(
		private subgroupService: ChapterSubgroupService,
		private chapterGroupService: ChapterGroupService,
		private loggedInUserService: LoggedInUserService,
		private userAuthenticationService: UserAuthenticationService,
		public router: Router
	) {
	}


	ngOnInit() {
		this.userAuthenticationService.getCurrentUser().subscribe(u => {
			this.loggedInUser = u;
			this.loggedInUserService.getStudentEnrollments().subscribe(e => this.enrollments = e);
		});
	}

}
