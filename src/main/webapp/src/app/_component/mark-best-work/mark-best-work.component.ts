import {Component, Input, OnInit} from '@angular/core';
import {UserAuthenticationService} from "../../_service/user-authentication.service";
import {User, UserRole} from "../../_dto/user";
import {Submission, SubmissionDetails} from "../../_dto/submission";
import {SubmissionService} from "../../_service/submission.service";

@Component({
	selector: 'mark-best-work',
	templateUrl: './mark-best-work.component.html',
	styleUrls: ['./mark-best-work.component.scss']
})
export class MarkBestWorkComponent implements OnInit {
	@Input() submission: SubmissionDetails;
	@Input() includeText: boolean = true;
	private loggedInUser: User;

	loading: boolean = false;

	constructor(
		private authenticationService: UserAuthenticationService,
		private submissionService: SubmissionService
	) {
	}

	ngOnInit() {
		this.authenticationService.getCurrentUser().subscribe(u => this.loggedInUser = u);
	}

	/**
	 * Toggles the best work state of the submission.
	 */
	toggleBestWork() {
		if (!this.loading) {
			this.loading = true;

			const dto: Submission = Object.assign(this.submission, {
				bestWork: !this.isBestWork
			});

			this.submissionService.updateSubmission(
				this.submission.task.chapterId,
				this.submission.chapterGroupId,
				this.submission.id,
				dto
			).subscribe(s => {
				this.submission = s
			}, () => {
			}, () => {
				this.loading = false;
			})
		}
	}

	/**
	 * Returns whether the submission is considered to be "best work".
	 * @returns {boolean}
	 */
	get isBestWork(): boolean {
		return this.submission.bestWork;
	}

	/**
	 * Returns whether the currently logged in user is a teacher.
	 * @returns {boolean}
	 */
	get isTeacher(): boolean {
		return this.loggedInUser.role === UserRole.TEACHER;
	}
}
