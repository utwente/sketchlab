import {Input} from '@angular/core';
import {SubmissionDetails} from '../../_dto/submission';
import {User, UserRole} from '../../_dto/user';
import {UserAuthenticationService} from '../../_service/user-authentication.service';
import {SubmissionFileInformation} from "../../_service/submission-file-information";

/**
 * Abstract Submission used to build a submission box. Implementations need to specify the type
 * of service to use, as these may require different thumbnail URLs.
 */
export abstract class SubmissionComponent<T extends SubmissionFileInformation> {
	@Input('submission') public submission: SubmissionDetails;
	@Input('includeOwner') public includeOwner: boolean;
	@Input() clickable : boolean = true;
	protected user: User;

	protected constructor(
		protected submissionType: SubmissionType,
		protected service: T,
		private userAuthenticationService: UserAuthenticationService) {
		this.userAuthenticationService.getCurrentUser().subscribe(user => this.user = user)
	}

	/**
	 * Generates thumbnail URL based on the given submission.
	 * @param {SubmissionDetails} submission
	 * @returns {string}
	 */
	generateThumbnailUrl(submission: SubmissionDetails) {
		return this.service.getThumbnailUrl(submission);
	}

	/**
	 * Returns whether the logged in user is a teacher.
	 * @returns {boolean}
	 */
	get isTeacher(): boolean {
		return this.user && this.user.role == UserRole.TEACHER;
	}

	/**
	 * Returns whether the logged in user is anonymous.
	 * @returns {boolean}
	 */
	get isAnonymous(): boolean {
		return this.user && this.user.role == UserRole.ANONYMOUS;
	}

	/**
	 * Returns whether the annotations/comments button should be displayed.
	 * @returns {boolean}
	 */
	get annotationsEnabled(): boolean {
		return this.submissionType === SubmissionType.REGULAR_SUBMISSION;
	}

	/**
	 * Returns the URL for the submission, for when the box is clickable.
	 * @returns {string}
	 */
	getRoute(): string {
		return this.service.getSubmissionUrl(this.submission);
	}
}

export enum SubmissionType {
	REGULAR_SUBMISSION, BEST_WORK_SUBMISSION
}
