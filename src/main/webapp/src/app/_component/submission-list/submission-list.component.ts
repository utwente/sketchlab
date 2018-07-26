import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {SubmissionDetails} from '../../_dto/submission';
import {SubmissionType} from "../submission/submission.component";
import {Page, PageParameters} from "../../_dto/page";

@Component({
	selector: 'submission-list',
	templateUrl: './submission-list.component.html',
	styleUrls: ['./submission-list.component.scss']
})
export class SubmissionListComponent implements OnInit {
	/**
	 * The submissions to show
	 */
	@Input() submissions: SubmissionDetails[];
	/**
	 * Whether or not to show the user information.
	 * @type {boolean}
	 */
	@Input() showUser: boolean = true;
	/**
	 * Whether the submission is clickable.
	 * @type {boolean}
	 */
	@Input() clickable: boolean = true;
	/**
	 * The type of submission.
	 * @type {SubmissionType.REGULAR_SUBMISSION}
	 */
	@Input() submissionType: SubmissionType = SubmissionType.REGULAR_SUBMISSION;

	/**
	 * Import enum as local type
	 * @type {SubmissionType}
	 */
	SubmissionType = SubmissionType;

	constructor() {
	}

	ngOnInit() {
	}
}
