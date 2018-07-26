import {Component, Input, OnInit} from '@angular/core';
import {SubmissionDetails} from "../../_dto/submission";
import {BestWorkService} from "../../_service/best-work.service";
import {SubmissionType} from "../submission/submission.component";

@Component({
	selector: 'best-work',
	templateUrl: './best-work.component.html',
	styleUrls: ['./best-work.component.scss']
})
export class BestWorkComponent implements OnInit {
	@Input() submissionAmount: number = 3;

	submissions: SubmissionDetails[];

	/**
	 * Import exported Enum as local enum, otherwise templates can not use enums.
	 * @type {SubmissionType}
	 */
	SubmissionType = SubmissionType;

	constructor(private bestWorkService: BestWorkService) {
	}

	ngOnInit() {
		this.bestWorkService
			.getBestWorkSubmissions(this.submissionAmount)
			.subscribe(submissions => {
				this.submissions = submissions
			});
	}
}
