import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';
import {Observable} from 'rxjs/Observable';
import {
	Ordering,
	Submission,
	SubmissionDetails,
	SubmissionTransformation
} from '../_dto/submission';
import {Task} from '../_dto/task';
import {AppConfig} from '../app.config';
import {SubmissionFileInformation} from "./submission-file-information";
import {Page, PageParameters} from "../_dto/page";

@Injectable()
export class SubmissionService implements SubmissionFileInformation {

	constructor(
		private http: HttpClient,
		private config: AppConfig,
		private domSanitizer: DomSanitizer
	) {
	}

	/**
	 * Return all submissions for the given chapter group and user.
	 * @param {number} chapterId The ID of the chapter to which the submissions belong.
	 * @param {number} chapterGroupId The ID of the chapter group to which the submissions belong.
	 * @param userId
	 * @param ordering
	 * @param pageParameters
	 * @returns {Observable<Page<SubmissionDetails>>} An observable containing a page with
	 * submissions.
	 */
	public getSubmissionsByUser(
		chapterId: number,
		chapterGroupId: number,
		userId: string,
		pageParameters: PageParameters,
		ordering: Ordering = Ordering.NEW
	): Observable<Page<SubmissionDetails>> {
		return this.http.get<Page<SubmissionDetails>>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/by-user/${userId}`,
			{
				params: new HttpParams({
					fromObject: {
						'ordering': ordering.toString(),
						'include-deleted': 'false',
						'pageOffset': `${pageParameters.offset}`,
						'pageSize': `${pageParameters.pageSize}`
					}
				})
			}
		);
	}

	/**
	 * Generates a thumbnail url based on the given submission.
	 * @param {SubmissionDetails} submission
	 * @returns {string}
	 */
	public getThumbnailUrl(submission: SubmissionDetails): string {
		return `${this.config.apiUrl}/chapters/${submission.task.chapterId}/groups/${submission.chapterGroupId}/submissions/${submission.id}/thumbnail`;
	}

	/**
	 * Generates a submission URL for uploading files.
	 * @param {Task} task
	 * @param {number} chapterGroupId
	 * @returns {string}
	 */
	public getSubmissionUploadUrl(task: Task, chapterGroupId: number): string {
		return `${this.config.apiUrl}/chapters/${task.chapterId}/groups/${chapterGroupId}/submissions`;
	}

	/**
	 * Return all submissions which the logged in user has made for a certain task in a certain
	 * chapter group.
	 * @param {Task} task The task
	 * @param {number} chapterGroupId The ID of the chapter group
	 * @param pageParameters
	 * @returns {Observable<Page<SubmissionDetails>>} An observable containing a page with
	 * submissions.
	 */
	public getOwnSubmissionsForTask(
		task: Task,
		chapterGroupId: number,
		pageParameters: PageParameters
	): Observable<Page<SubmissionDetails>> {
		return this.http.get<Page<SubmissionDetails>>(
			`${this.config.apiUrl}/chapters/${task.chapterId}/groups/${chapterGroupId}/submissions/by-task/${task.id}/me`,
			{
				params: new HttpParams({
					fromObject: {
						'pageOffset': `${pageParameters.offset}`,
						'pageSize': `${pageParameters.pageSize}`
					}
				})
			}
		);
	}

	// noinspection JSMethodCanBeStatic
	/**
	 * Returns the URL at which the given submission can be found.
	 * @param {SubmissionDetails} submission
	 * @returns {string}
	 */
	getSubmissionUrl(submission: SubmissionDetails): string {
		const chapterId = submission.task.chapterId;
		const groupId = submission.chapterGroupId;
		const submissionId = submission.id;
		return `/courses/${chapterId}/editions/${groupId}/submissions/${submissionId}`;
	}

	/**
	 * Return all submissions for the given chapter and chaptergroup.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param ordering
	 * @param pageParameters
	 * @returns {Observable<Page<SubmissionDetails>>} An observable containing a page with
	 * submissions.
	 */
	public getSubmissions(
		chapterId: number,
		chapterGroupId: number,
		ordering: Ordering,
		pageParameters: PageParameters
	): Observable<Page<SubmissionDetails>> {
		return this.http.get<Page<SubmissionDetails>>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions`,
			{
				params: new HttpParams({
					fromObject: {
						'ordering': ordering,
						'include-deleted': 'false',
						'pageOffset': `${pageParameters.offset}`,
						'pageSize': `${pageParameters.pageSize}`
					}
				})
			}
		);
	}

	/**
	 * Return all submissions for the given chapter, chapter group and subgroup.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} subgroupId
	 * @param ordering
	 * @param pageParameters
	 * @returns {Observable<Page<SubmissionDetails>>} An observable containing a page with
	 * submissions.
	 */
	public getSubmissionsBySubgroup(
		chapterId: number,
		chapterGroupId: number,
		subgroupId: number,
		ordering: Ordering,
		pageParameters: PageParameters
	): Observable<Page<SubmissionDetails>> {
		return this.http.get<Page<SubmissionDetails>>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/by-subgroup/${subgroupId}`,
			{
				params: new HttpParams({
					fromObject: {
						'ordering': ordering,
						'include-deleted': 'false',
						'pageOffset': `${pageParameters.offset}`,
						'pageSize': `${pageParameters.pageSize}`
					}
				})
			}
		);
	}


	/**
	 * Retrieves the meta-data of a submission for the given parameters.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @returns {Observable<SubmissionDetails>}
	 */
	getSubmission(chapterId: number, chapterGroupId: number, submissionId: number) {
		return this.http.get<SubmissionDetails>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}`
		);
	}

	/**
	 * Adds a vote for the logged in user to the given submission.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @returns {Observable<SubmissionDetails>}
	 */
	vote(
		chapterId: number,
		chapterGroupId: number,
		submissionId: number
	): Observable<SubmissionDetails> {
		return this.http.put<SubmissionDetails>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}/vote`,
			{}
		);
	}

	/**
	 * Removes the vote of the logged in user for the given submission.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @returns {Observable<SubmissionDetails>}
	 */
	unvote(
		chapterId: number,
		chapterGroupId: number,
		submissionId: number
	): Observable<SubmissionDetails> {
		return this.http.delete<SubmissionDetails>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}/vote`
		);
	}

	/**
	 * Transforms a submission, and all belonging annotations.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @param {SubmissionTransformation} transformation
	 * @returns {Observable<Object>}
	 */
	transformSubmission(
		chapterId: number,
		chapterGroupId: number,
		submissionId: number,
		transformation: SubmissionTransformation
	) {
		return this.http.put(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}/file?transformation=${transformation}`, {}
		);
	}

	/**
	 * Returns the file for the given submission as a data URL.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @returns {Observable<SafeUrl>}
	 */
	getSubmissionDataUrl(chapterId: number, chapterGroupId: number, submissionId: number): Observable<SafeUrl> {
		return this.http.get(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}/file`,
			{responseType: 'blob'}
		).map(e => this.domSanitizer.bypassSecurityTrustUrl(URL.createObjectURL(e)));
	}

	/**
	 * Updates the submission belonging to the given parameters.
	 * @param {number} chapterId
	 * @param {number} chapterGroupId
	 * @param {number} submissionId
	 * @param {Submission} updatedSubmission
	 * @returns {Observable<Submission>}
	 */
	updateSubmission(
		chapterId: number,
		chapterGroupId: number,
		submissionId: number,
		updatedSubmission: Submission
	): Observable<SubmissionDetails> {
		return this.http.put<SubmissionDetails>(
			`${this.config.apiUrl}/chapters/${chapterId}/groups/${chapterGroupId}/submissions/${submissionId}`,
			updatedSubmission
		);
	}
}
