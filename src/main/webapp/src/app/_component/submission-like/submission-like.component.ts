import {Component, Input, OnInit} from '@angular/core';
import {SubmissionDetails} from '../../_dto/submission';
import {User, UserRole} from '../../_dto/user';
import {SubmissionService} from '../../_service/submission.service';
import {UserAuthenticationService} from '../../_service/user-authentication.service';

@Component({
	selector: 'submission-like',
	templateUrl: './submission-like.component.html',
	styleUrls: ['./submission-like.component.scss']
})
export class SubmissionLikeComponent implements OnInit {
	@Input() submission: SubmissionDetails;
	@Input() includeText: boolean = true;
	loggedInUser: User;
	loadingVote: boolean = false;

	constructor(
		private userAuthenticationService: UserAuthenticationService,
		private submissionService: SubmissionService) {
	}

	ngOnInit() {
		this.userAuthenticationService.getCurrentUser()
			.subscribe(user => this.loggedInUser = user);
	}

	toggleVote() {
		if (!this.loadingVote) {
			this.loadingVote = true;
			const voteObservable = this.submission.userHasVoted
				? this.submissionService.unvote(
					this.submission.task.chapterId,
					this.submission.chapterGroupId,
					this.submission.id
				)
				: this.submissionService.vote(
					this.submission.task.chapterId,
					this.submission.chapterGroupId,
					this.submission.id
				);
			voteObservable.subscribe(
				submission => this.submission = submission,
				() => {
				},
				() => {
					this.loadingVote = false;
				}
			);
		}
	}

	get loggedInUserIsAnonymous(): boolean {
		return this.loggedInUser && this.loggedInUser.role == UserRole.ANONYMOUS;
	}
}
