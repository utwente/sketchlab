import {TitleCasePipe} from '@angular/common';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {combineLatest} from 'rxjs/observable/combineLatest';
import {ListItem} from '../../_component/option-select/option-select.component';
import {EnrollmentDetails} from '../../_dto/enrollment';
import {Ordering, SubmissionDetails} from '../../_dto/submission';
import {User, UserRole} from '../../_dto/user';
import {ChapterGroupEnrollmentService} from '../../_service/chapter-group-enrollment.service';
import {SubmissionService} from '../../_service/submission.service';
import {UserAuthenticationService} from '../../_service/user-authentication.service';
import {UserService} from '../../_service/user.service';
import {enumValues} from '../../utils/enum-values';
import {Page, PageParameters} from "../../_dto/page";

@Component({
	selector: 'app-portfolio',
	templateUrl: './portfolio.component.html',
	styleUrls: ['./portfolio.component.scss']
})
export class PortfolioComponent implements OnInit {
	chapterId: number;
	chapterGroupId: number;

	user: User;
	enrollment: EnrollmentDetails;
	submissionsPage: Page<SubmissionDetails>;
	ordering: Ordering;

	loggedInUser: User;

	sortOptions: ListItem<'BEST' | 'NEW' | 'TASK'>[] = Array
		.from(enumValues(Ordering))
		.map(o => new ListItem(this.titleCasePipe.transform(o.toString()), o));

	constructor(
		private userAuthenticationService: UserAuthenticationService,
		private enrollmentService: ChapterGroupEnrollmentService,
		private userService: UserService,
		private submissionService: SubmissionService,
		private titleCasePipe: TitleCasePipe,
		private route: ActivatedRoute
	) {
	}

	ngOnInit() {
		this.route.params.subscribe(params => {
			this.chapterId = +params['chapterId'];
			this.chapterGroupId = +params['chapterGroupId'];

			const userId = params['userId'];

			this.userService.getUser(userId).subscribe(user => {
				this.user = user;
				this.getSubmissions(Ordering.NEW);
			});

			combineLatest(this.userAuthenticationService.getCurrentUser(), this.userService.getUser(userId))
				.subscribe(([loggedInUser, user]) => {
					this.loggedInUser = loggedInUser;
					this.user = user;

					if (this.isTeacher() || this.isOwnUser()) {
						this.enrollmentService
							.getEnrollment(this.chapterId, this.chapterGroupId, userId)
							.subscribe(enrollment => {
								this.enrollment = enrollment
							});
					}
				});
		});
	}

	/**
	 * Returns all submissions for the given user in the requested chapter and chapter group.
	 * @param {Ordering} ordering
	 * @param params
	 */
	getSubmissions(ordering: Ordering, params: PageParameters = {offset: 0, pageSize: 15}) {
		this.ordering = ordering;
		this.submissionService
			.getSubmissionsByUser(
				this.chapterId,
				this.chapterGroupId,
				this.user.id,
				params,
				ordering
			)
			.subscribe(submissions => {
				this.submissionsPage = submissions
			});
	}

	/**
	 * Returns true if the currently logged in user is the same as the requested portfolio's user.
	 * @returns {boolean}
	 */
	isOwnUser(): boolean {
		return this.user && this.loggedInUser && this.user.id === this.loggedInUser.id;
	}

	/**
	 * Returns true if the logged in user is a teacher.
	 * @returns {boolean}
	 */
	isTeacher(): boolean {
		return this.loggedInUser && this.loggedInUser.role == UserRole.TEACHER;
	}

	/**
	 * Determines whether the grade box should be shown.
	 * @returns {boolean}
	 */
	showGrade(): boolean {
		return this.enrollment && (this.isOwnUser() || this.isTeacher());
	}
}
