import {Component} from '@angular/core';
import {SubmissionComponent, SubmissionType} from "../submission.component";
import {SubmissionService} from "../../../_service/submission.service";
import {UserAuthenticationService} from "../../../_service/user-authentication.service";

/**
 * Creates a submission box while using SubmissionService as it's service.
 */
@Component({
	selector: 'submission',
	templateUrl: '../submission.component.html',
	styleUrls: ['../submission.component.scss']
})
export class RegularSubmissionComponent extends SubmissionComponent<SubmissionService> {

	constructor(
		submissionService: SubmissionService,
		userAuthenticationService: UserAuthenticationService
	) {
		super(SubmissionType.REGULAR_SUBMISSION, submissionService, userAuthenticationService)
	}

	ngOnInit() {
	}
}
