import {Component} from '@angular/core';
import {SubmissionComponent, SubmissionType} from "../submission.component";
import {BestWorkService} from "../../../_service/best-work.service";
import {UserAuthenticationService} from "../../../_service/user-authentication.service";

/**
 * Creates a submission box while using BestWorkService as it's service.
 */
@Component({
	selector: 'best-work-submission',
	templateUrl: '../submission.component.html',
	styleUrls: ['../submission.component.scss']
})
export class BestWorkSubmissionComponent extends SubmissionComponent<BestWorkService> {
	constructor(
		bestWorkService: BestWorkService,
		userAuthenticationService: UserAuthenticationService
	) {
		super(SubmissionType.BEST_WORK_SUBMISSION, bestWorkService, userAuthenticationService)
	}

	ngOnInit() {
	}
}
