import {TitleCasePipe} from '@angular/common';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {ListItem} from '../../_component/option-select/option-select.component';
import {Ordering, SubmissionDetails} from '../../_dto/submission';
import {SubmissionService} from '../../_service/submission.service';
import {enumValues} from '../../utils/enum-values';
import {Page, PageParameters} from "../../_dto/page";

@Component({
	selector: 'app-work',
	templateUrl: './work.component.html',
	styleUrls: ['./work.component.scss']
})
export class WorkComponent implements OnInit {
	chapterId: number;
	chapterGroupId: number;
	subgroupId: number;

	submissionsPage: Page<SubmissionDetails>;
	ordering: Ordering;
	/**
	 * Creates sort options.
	 * @type {ListItem<any>[]}
	 */
	sortOptions: ListItem<'BEST' | 'NEW' | 'TASK'>[] = Array
		.from(enumValues(Ordering))
		.map(o => new ListItem(this.titleCasePipe.transform(o.toString()), o));

	constructor(
		private submissionService: SubmissionService,
		private titleCasePipe: TitleCasePipe,
		private route: ActivatedRoute
	) {
	}

	/**
	 * Sets all parameters and retrieves the submissions on init.
	 */
	ngOnInit() {
		this.route.params.subscribe(params => {
			this.chapterId = +params['chapterId'];
			this.chapterGroupId = +params['chapterGroupId'];
			this.subgroupId = +params['subgroupId'];

			this.getSubmissions(Ordering.BEST);

		});
	}

	/**
	 * Gets submissions based on the current route. When a subgroup ID is present returns only
	 * submissions for the given subgroup, otherwise returns all submissions for the whole chapter
	 * group.
	 * @param {Ordering} ordering
	 * @param params
	 */
	getSubmissions(ordering: Ordering, params: PageParameters = {offset: 0, pageSize: 15}) {
		this.ordering = ordering;
		const observableSubmissions = !!this.subgroupId
			? this.getSubmissionsBySubgroup(ordering, params)
			: this.getSubmissionsByChapterGroup(ordering, params);
		observableSubmissions.subscribe(submissions => this.submissionsPage = submissions);
	}

	/**
	 * Retrieves submissions by subgroup and a given ordering.
	 * @param {Ordering} ordering
	 * @param params
	 * @returns {Observable<SubmissionDetails[]>}
	 */
	getSubmissionsBySubgroup(
		ordering: Ordering,
		params: PageParameters
	): Observable<Page<SubmissionDetails>> {
		return this.submissionService
			.getSubmissionsBySubgroup(
				this.chapterId,
				this.chapterGroupId,
				this.subgroupId,
				ordering,
				params
			);
	}

	/**
	 * Retrieves submissions by chapter group and a given ordering.
	 * @param {Ordering} ordering
	 * @param params
	 * @returns {Observable<SubmissionDetails[]>}
	 */
	getSubmissionsByChapterGroup(
		ordering: Ordering,
		params: PageParameters
	): Observable<Page<SubmissionDetails>> {
		return this.submissionService.getSubmissions(
			this.chapterId,
			this.chapterGroupId,
			ordering,
			params
		);
	}
}
