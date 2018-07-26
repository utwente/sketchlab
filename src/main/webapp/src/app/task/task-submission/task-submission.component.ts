import {Component, Input, OnInit} from '@angular/core';
import {SubmissionDetails} from '../../_dto/submission';
import {Task} from '../../_dto/task';
import {SubmissionService} from '../../_service/submission.service';
import {Page, PageParameters} from "../../_dto/page";
import {RouterStateService} from "../../_service/router-state.service";
import {Router} from "@angular/router";

@Component({
	selector: 'task-submission',
	templateUrl: './task-submission.component.html',
	styleUrls: ['./task-submission.component.scss']
})
export class TaskSubmissionComponent implements OnInit {

	@Input() task: Task;
	@Input() chapterGroupId: number;
	submissionsPage: Page<SubmissionDetails>;

	constructor(
		private submissionService: SubmissionService,
		private router: Router,
		private routerState: RouterStateService) {
	}

	ngOnInit() {
		this.getSubmissions();
	}

	/**
	 * Retrieves all submissions done by the logged in user for the given task in the given chapter
	 * group.
	 */
	getSubmissions(params: PageParameters = {offset: 0, pageSize: 15}) {
		this.submissionService.getOwnSubmissionsForTask(this.task, this.chapterGroupId, params)
			.subscribe(submissions => this.submissionsPage = submissions);
	}

	onUpload(submission: SubmissionDetails) {
		this.routerState.save(this.router.routerState.snapshot.url);
		this.router.navigate([
			'/courses', this.task.chapterId,
			'editions', this.chapterGroupId,
			'submissions', submission.id
		]);
	}

	/**
	 * Returns the URL where to upload the submission to.
	 * @returns {string}
	 */
	getUploadUrl(): string {
		return this.submissionService.getSubmissionUploadUrl(this.task, this.chapterGroupId);
	}

	/**
	 * Returns the query parameters on the POST request on a file upload.
	 * @returns {any}
	 */
	getUploadData(): any {
		return {'taskId': this.task.id};
	}
}
