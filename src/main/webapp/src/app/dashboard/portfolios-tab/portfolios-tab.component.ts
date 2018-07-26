import {Component, OnInit} from '@angular/core';
import {ListItem} from '../../_component/option-select/option-select.component';
import {ChapterGroup} from '../../_dto/chapter';
import {SubmissionDetails} from '../../_dto/submission';
import {User} from '../../_dto/user';
import {LoggedInUserService} from "../../_service/logged-in-user.service";
import {SubmissionService} from '../../_service/submission.service';
import {UserAuthenticationService} from '../../_service/user-authentication.service';
import {Page, PageParameters} from "../../_dto/page";

@Component({
	selector: 'app-portfolios-tab',
	templateUrl: './portfolios-tab.component.html',
	styleUrls: ['./portfolios-tab.component.scss']
})
export class PortfoliosTabComponent implements OnInit {
	user: User;
	submissionsPage: Page<SubmissionDetails>;
	submissionsLoaded: boolean = true;
	filterEnrollments: ListItem<ChapterGroup>[];
	chapterGroup: ChapterGroup;
	init: boolean = true;

	constructor(private submissionService: SubmissionService,
				private loggedInService: LoggedInUserService,
				userAuthenticationService: UserAuthenticationService) {
		this.loggedInService.getStudentEnrollments()
			.subscribe(enrollments => {
				this.filterEnrollments = enrollments
					.map(e => e.chapterGroup)
					.map(c => new ListItem<ChapterGroup>(c.name, c));
			});
		userAuthenticationService.getCurrentUser().subscribe((user) => this.user = user);

	}

	ngOnInit() {
	}

	public showSubmissions(chapterGroup: ChapterGroup) {
		this.chapterGroup = chapterGroup;
		const pageParams: PageParameters = {
			offset: 0,
			pageSize: 15
		};
		this.getSubmissions(chapterGroup.chapterId, chapterGroup.id, this.user.id, pageParams);
	}

	/**
	 * Get submissions for the given parameters and
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {string} userId
	 * @param {PageParameters} pageParameters
	 */
	getSubmissions(
		chapterId: number,
		chapterGroupId: number,
		userId: string,
		pageParameters: PageParameters
	) {
		this.init = false;
		this.submissionsLoaded = false;
		this.submissionService.getSubmissionsByUser(
			chapterId,
			chapterGroupId,
			userId,
			pageParameters)
			.subscribe((submissions) => {
				this.submissionsLoaded = true;
				this.submissionsPage = submissions;
			});
	}
}
